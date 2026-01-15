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

        removeMessage(chatId, messageId) {
            if (!this.messagesByChat[chatId]) return
            this.messagesByChat[chatId] = this.messagesByChat[chatId].filter(
                (msg) => msg.id !== messageId
            )
        },

        updateMessage(chatId, messageId, newContent) {
            if (!this.messagesByChat[chatId]) return
            const message = this.messagesByChat[chatId].find((msg) => msg.id === messageId)
            if (message) {
                message.content = newContent
                message.edited = true
            }
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