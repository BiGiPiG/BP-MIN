// utils/useChats.js
import { ref } from 'vue'

export function useChats() {
    const chats = ref([])
    const loading = ref(false)
    const error = ref(null)

    const fetchChats = async () => {
        const accessToken = localStorage.getItem('accessToken')
        loading.value = true
        error.value = null

        try {
            const response = await fetch("/api/chats", {
                method: "GET",
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                }
            })

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`)
            }

            const data = await response.json()

            chats.value = Array.isArray(data) ? data : []

        } catch (err) {
            error.value = err
            chats.value = []
            console.error('Failed to load chats', err)
        } finally {
            console.log(chats.value)
            loading.value = false
        }
    }

    const createChat = async ({ type, title, participants }) => {
        const accessToken = localStorage.getItem('accessToken')
        loading.value = true
        error.value = null

        console.log('Creating chat with:', { type, title, participants });

        try {
            const response = await fetch("/api/chats/create", {
                method: "POST",
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    type,
                    title,
                    participants
                })
            })

            if (!response.ok) {
                const errorMsg = await response.text()
                throw new Error(`HTTP ${response.status}: ${errorMsg || response.statusText}`)
            }

            const newChat = await response.json()
            chats.value.push(newChat)
            return newChat
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