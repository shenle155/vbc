import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAlarmStore = defineStore('alarm', () => {
  const unreadCount = ref(0)

  function incrementUnread() {
    unreadCount.value++
  }

  function resetUnread() {
    unreadCount.value = 0
  }

  function setUnread(count: number) {
    unreadCount.value = count
  }

  return { unreadCount, incrementUnread, resetUnread, setUnread }
})
