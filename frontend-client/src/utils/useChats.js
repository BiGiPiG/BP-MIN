// utils/useChats.js
import { ref } from 'vue'
import { chatsApi } from '@/api'

export function useChats() {
    const chats = ref([])
    const loading = ref(false)
    const error = ref(null)

    const fetchChats = async () => {
        loading.value = true
        error.value = null

        try {
            const data = await chatsApi.getChats()
            chats.value = Array.isArray(data) ? data : []
        } catch (err) {
            error.value = err
            chats.value = []
            console.error('Failed to load chats', err)
        } finally {
            loading.value = false
        }
    }

    const createChat = async (chatData) => {
        loading.value = true
        error.value = null

        try {
            return await chatsApi.createChat(chatData)
        } catch (err) {
            error.value = err
            console.error('Failed to create chat', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    return { chats, loading, error, fetchChats, createChat }
}
