import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useWebSocketStore = defineStore('websocket', () => {
  const connected = ref(false)
  const token = ref('')
  const subscriptions = ref<Set<string>>(new Set())

  function setConnected(value: boolean) {
    connected.value = value
  }

  function setToken(value: string) {
    token.value = value
  }

  function addSubscription(topic: string) {
    subscriptions.value.add(topic)
  }

  function removeSubscription(topic: string) {
    subscriptions.value.delete(topic)
  }

  function clearSubscriptions() {
    subscriptions.value.clear()
  }

  return {
    connected,
    token,
    subscriptions,
    setConnected,
    setToken,
    addSubscription,
    removeSubscription,
    clearSubscriptions,
  }
})
