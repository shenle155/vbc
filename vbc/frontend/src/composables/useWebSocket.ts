import { ref, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getWsToken } from '@/api/websocket'
import { useWebSocketStore } from '@/store/websocket'

type MessageHandler = (body: any) => void

export function useWebSocket() {
  const client = ref<Client | null>(null)
  const connected = ref(false)
  const reconnectAttempts = ref(0)
  const maxReconnectDelay = 30000
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null
  const handlers = new Map<string, Set<MessageHandler>>()

  async function connect() {
    try {
      const res = await getWsToken()
      const { token, brokerUrl } = res.data.data

      const wsStore = useWebSocketStore()
      wsStore.setToken(token)

      const stompClient = new Client({
        webSocketFactory: () => new SockJS(brokerUrl),
        connectHeaders: { token },
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        debug: () => {},
        onConnect: () => {
          connected.value = true
          reconnectAttempts.value = 0
          wsStore.setConnected(true)
          // Re-subscribe to all previously active topics
          handlers.forEach((_, topic) => {
            stompClient.subscribe(topic, (message) => {
              try {
                const body = JSON.parse(message.body)
                const subHandlers = handlers.get(topic)
                if (subHandlers) {
                  subHandlers.forEach((h) => h(body))
                }
              } catch { /* ignore parse errors */ }
            })
          })
        },
        onDisconnect: () => {
          connected.value = false
          wsStore.setConnected(false)
          scheduleReconnect()
        },
        onStompError: () => {
          connected.value = false
          scheduleReconnect()
        },
      })

      stompClient.activate()
      client.value = stompClient
    } catch {
      scheduleReconnect()
    }
  }

  function scheduleReconnect() {
    if (reconnectTimer) return
    const delay = Math.min(
      Math.pow(2, reconnectAttempts.value) * 1000,
      maxReconnectDelay
    )
    reconnectAttempts.value++
    reconnectTimer = setTimeout(() => {
      reconnectTimer = null
      connect()
    }, delay)
  }

  function subscribe(topic: string, handler: MessageHandler) {
    if (!handlers.has(topic)) {
      handlers.set(topic, new Set())
      useWebSocketStore().addSubscription(topic)
    }
    handlers.get(topic)!.add(handler)

    // Subscribe on an active connection
    if (client.value?.connected) {
      client.value.subscribe(topic, (message) => {
        try {
          const body = JSON.parse(message.body)
          const subs = handlers.get(topic)
          if (subs) subs.forEach((h) => h(body))
        } catch { /* ignore */ }
      })
    }
  }

  function unsubscribe(topic: string, handler?: MessageHandler) {
    if (handler && handlers.has(topic)) {
      handlers.get(topic)!.delete(handler)
    } else if (!handler) {
      handlers.delete(topic)
      useWebSocketStore().removeSubscription(topic)
    }
  }

  function send(destination: string, body: any) {
    if (client.value?.connected) {
      client.value.publish({ destination, body: JSON.stringify(body) })
    }
  }

  function disconnect() {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    client.value?.deactivate()
    handlers.clear()
    useWebSocketStore().clearSubscriptions()
  }

  onUnmounted(() => {
    disconnect()
  })

  return { connected, connect, disconnect, subscribe, unsubscribe, send }
}
