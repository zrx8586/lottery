import { reactive } from 'vue'

const state = reactive({
  events: {}
})

const eventBus = {
  $on(event, callback) {
    if (!state.events[event]) {
      state.events[event] = []
    }
    state.events[event].push(callback)
  },
  $off(event, callback) {
    if (!state.events[event]) return
    if (!callback) {
      state.events[event] = []
    } else {
      state.events[event] = state.events[event].filter(cb => cb !== callback)
    }
  },
  $emit(event, ...args) {
    if (!state.events[event]) return
    state.events[event].forEach(callback => {
      callback(...args)
    })
  }
}

export default eventBus 