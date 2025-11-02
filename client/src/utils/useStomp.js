import { ref } from 'vue'
import { Client } from '@stomp/stompjs'

export function useStomp() {
    const client = ref(null)
    const isConnected = ref(false)
    const messages = ref([])
    const error = ref(null)
    const subscriptions = ref(new Map())

    const connect = (brokerURL, connectHeaders = {}, onConnectCallback = null) => {
        return new Promise((resolve, reject) => {
            try {
                if (client.value) {
                    disconnect()
                }

                const stompClient = new Client({
                    brokerURL,
                    reconnectDelay: 5000,
                    heartbeatIncoming: 4000,
                    heartbeatOutgoing: 4000,
                    connectionTimeout: 5000,
                    onConnect: () => {
                        isConnected.value = true
                        console.log('STOMP connected')
                        if (typeof onConnectCallback === 'function') {
                            try {
                                onConnectCallback()
                            } catch (cbError) {
                                console.error('Error in onConnect callback:', cbError)
                            }
                        }
                        resolve(true)
                    },
                    onStompError: (frame) => {
                        const msg = frame.headers['message'] || 'STOMP error occurred'
                        error.value = msg
                        reject(new Error(msg))
                    },
                    onWebSocketError: () => {
                        const msg = 'WebSocket connection error'
                        error.value = msg
                        reject(new Error(msg))
                    },
                })

                if (Object.keys(connectHeaders).length > 0) {
                    stompClient.connectHeaders = connectHeaders
                }

                client.value = stompClient
                client.value.activate()
            } catch (err) {
                error.value = err.message || 'Unexpected error during STOMP connection'
                reject(err)
            }
        })
    }

    const disconnect = () => {
        return new Promise((resolve) => {
            if (client.value) {
                subscriptions.value.forEach((subscription) => {
                    if (subscription && typeof subscription.unsubscribe === 'function') {
                        subscription.unsubscribe()
                    }
                })
                subscriptions.value.clear()

                client.value.deactivate()
                client.value = null
                isConnected.value = false
                error.value = null

                console.log('STOMP fully disconnected')
                resolve(true)
            } else {
                resolve(false)
            }
        })
    }

    const subscribe = (destination, callback) => {
        if (!client.value || !isConnected.value) {
            console.error('STOMP client not connected');
            return null;
        }

        const subscription = client.value.subscribe(destination, (message) => {
            console.log(`📥 [${destination}] RAW message.body:`, message.body);
            console.log(`   typeof body:`, typeof message.body);

            try {
                const body = typeof message.body === 'string'
                    ? JSON.parse(message.body)
                    : message.body;
                console.log(`📦 [${destination}] Parsed:`, body);
                callback(body);
            } catch (e) {
                console.error(`❌ [${destination}] Failed to parse:`, message.body, e);
            }
        });

        subscriptions.value.set(destination, subscription);
        return () => unsubscribe(destination);
    };

    const subscribeToAllChats = (chats, messageCallback) => {
        if (!chats || !Array.isArray(chats)) {
            console.error('Invalid chats array')
            return []
        }

        const unsubscribeFunctions = []

        chats.forEach(chat => {
            if (chat && chat.id) {
                const destination = `/topic/chat/${chat.id}`
                console.log(`🔗 Subscribing to chat: ${chat.id}`)

                const unsubscribeFn = subscribe(destination, (message) => {
                    const messageWithChatId = {
                        ...message,
                        chatId: chat.id,
                        chatName: chat.name || `Chat ${chat.id}`
                    }
                    messageCallback(messageWithChatId)
                })

                if (unsubscribeFn) {
                    unsubscribeFunctions.push(unsubscribeFn)
                }
            }
        })

        console.log(`✅ Subscribed to ${unsubscribeFunctions.length} chats`)
        return unsubscribeFunctions
    }

    const subscribeToChat = (chatId, callback) => {
        const destination = `/topic/chat/${chatId}`
        return subscribe(destination, callback)
    }

    const unsubscribeFromAllChats = () => {
        const chatSubscriptions = Array.from(subscriptions.value.entries())
            .filter(([destination]) => destination.startsWith('/topic/chat/'))

        chatSubscriptions.forEach(([destination]) => {
            unsubscribe(destination)
        })

        console.log(`🔴 Unsubscribed from ${chatSubscriptions.length} chats`)
    }

    const unsubscribeFromChat = (chatId) => {
        const destination = `/topic/chat/${chatId}`
        unsubscribe(destination)
    }

    const getSubscribedChats = () => {
        return Array.from(subscriptions.value.keys())
            .filter(destination => destination.startsWith('/topic/chat/'))
            .map(destination => {
                const chatId = destination.replace('/topic/chat/', '')
                return { destination, chatId }
            })
    }

    const unsubscribe = (destination) => {
        const subscription = subscriptions.value.get(destination)
        if (subscription) {
            subscription.unsubscribe()
            subscriptions.value.delete(destination)
        }
    }

    const send = (destination, body, headers = {}) => {
        if (client.value && isConnected.value) {
            client.value.publish({
                destination,
                body: JSON.stringify(body),
                headers
            })
        } else {
            console.error('Cannot send message: STOMP client not connected')
        }
    }

    return {
        client,
        isConnected,
        messages,
        error,
        connect,
        disconnect,
        subscribe,
        unsubscribe,
        subscribeToAllChats,
        subscribeToChat,
        unsubscribeFromAllChats,
        unsubscribeFromChat,
        getSubscribedChats,
        send
    }
}