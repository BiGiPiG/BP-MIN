import { defineStore } from 'pinia'

export const useMessageStore = defineStore('messages', {
    state: () => ({
        messagesByChat: {}
    }),

    actions: {
        async loadMessages(chatId) {
            if (this.messagesByChat[chatId]) return

            const response = await fetch(`/api/chats/history/${chatId}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
            })
            this.messagesByChat[chatId] = await response.json()
        },

        addMessage(chatId, message) {
            if (!this.messagesByChat[chatId]) {
                this.messagesByChat[chatId] = []
            }
            this.messagesByChat[chatId].push(message)
        },

        clear() {
            this.messagesByChat = {}
        }
    },

    getters: {
        getMessages: (state) => (chatId) => {
            return state.messagesByChat[chatId] || []
        }
    }
})