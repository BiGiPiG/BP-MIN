import { Client } from '@stomp/stompjs'

class WebSocketService {
    constructor() {
        this.client = null
        this.isConnected = false
    }

    async connect(brokerURL, connectHeaders = {}) {
        return new Promise((resolve, reject) => {
            try {
                if (this.client) {
                    this.disconnect()
                }

                this.client = new Client({
                    brokerURL,
                    reconnectDelay: 5000,
                    heartbeatIncoming: 4000,
                    heartbeatOutgoing: 4000,
                    connectionTimeout: 5000,
                    onConnect: () => {
                        this.isConnected = true
                        console.log('STOMP connected')
                        resolve(true)
                    },
                    onStompError: (frame) => {
                        reject(new Error(frame.headers['message'] || 'STOMP error occurred'))
                    },
                    onWebSocketError: (event) => {
                        reject(new Error('WebSocket connection error'))
                    }
                })

                if (Object.keys(connectHeaders).length > 0) {
                    this.client.connectHeaders = connectHeaders
                }

                this.client.activate()
            } catch (err) {
                reject(err)
            }
        })
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate()
            this.client = null
            this.isConnected = false
        }
    }
}

export const websocketService = new WebSocketService()