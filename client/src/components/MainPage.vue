<script setup>

import {computed, onMounted, onUnmounted, ref} from 'vue'
import { useRouter } from 'vue-router'

import ChatList from '@/components/ChatList.vue'
import SearchBar from '@/components/SearchBar.vue'

import { useChats } from '@/utils/useChats.js'
import { useStomp } from '@/utils/useStomp.js'
import { useMessageStore } from '@/utils/useMessages.js'
import ConversationView from "@/components/conversation/ConversationView.vue";

// ─────────────────────────────────────
// Setup
// ─────────────────────────────────────

const router = useRouter()

// Reactive state
const activeChatName = ref('')
const activeChat = ref(null)
const activeChatSubscriptions = ref([])
const interlocutor = ref('')
const interlocutorStatus = ref('')

// Composable
const { chats, loading, error, fetchChats, createChat } = useChats()
const messageStore = useMessageStore()
const {
  connect,
  disconnect,
  subscribeToAllChats,
  unsubscribeFromAllChats,
  subscribe,
  send,
  subscribeToChat
} = useStomp()

// ─────────────────────────────────────
// Helper functions
// ─────────────────────────────────────

const profilePath = computed(() => {
  const username = localStorage.getItem('username')
  return username ? `/bp-min/${username}/profile` : '/profile'
})

const redirectToLogin = () => {
  router.push({ name: 'Login' })
}

const handleMessage = (message) => {
  const parsedMessage = typeof message === 'string' ? JSON.parse(message) : message
  parsedMessage.isSend = true
  messageStore.addMessage(parsedMessage.chatId, parsedMessage)
}

const handleNewChat = (newChat) => {
  const exists = chats.value.some(chat => chat?.id === newChat.id)
  if (!exists) {
    chats.value.unshift(newChat)
    subscribeToChat(newChat.id, handleMessage)
  }
}

const initializeWebSocket = async (token) => {
  const username = localStorage.getItem('username')
  const connectHeaders = { Authorization: `Bearer ${token}` }

  await connect('ws://localhost:8080/chats', connectHeaders, () => {
    subscribe(`/topic/user/${username}/chats`, handleNewChat)

    const chatList = Array.isArray(chats.value) ? chats.value : []
    if (chatList.length > 0) {
      subscribeToAllChats(chatList, handleMessage)
    }
  })
}

// ─────────────────────────────────────
// Lifecycle hooks
// ─────────────────────────────────────

onMounted(async () => {
  await fetchChats()

  const token = localStorage.getItem('accessToken')
  if (!token) {
    redirectToLogin()
    return
  }

  try {
    await initializeWebSocket(token)
  } catch (err) {
    console.error('WebSocket connection failed:', err)
    redirectToLogin()
  }
})

onUnmounted(() => {
  unsubscribeFromAllChats()
  disconnect()
})

// ─────────────────────────────────────
// Event handlers
// ─────────────────────────────────────

const handleMessageRead = async ({ messageId, chatId }) => {
  try {
    send('/bp-min/chat.readMessage', {
      messageId: messageId,
      chatId: chatId
    })
  } catch (error) {
    console.error('Failed to mark message as read:', error)
  }
}

const handleChatSelected = (chat, chatName) => {
  const userId = localStorage.getItem('userId')
  activeChatSubscriptions.value.forEach(unsub => unsub())
  activeChatSubscriptions.value = []

  activeChatName.value = chatName
  activeChat.value = chat
  interlocutor.value = chat?.participantInfo?.find(p => p.nickname === chatName)?.username

  if (!chat?.id) return

  const chatId = chat.id

  activeChatSubscriptions.value.push(
      subscribe(`/topic/chat/${chatId}/edited`, (payload) => {
        messageStore.updateMessage(chatId, payload.messageId, payload.newContent)
      }),
      subscribe(`/topic/chat/${chatId}/deleted`, (payload) => {
        messageStore.removeMessage(chatId, payload.messageId)
      }),
      subscribe(`/topic/chat/${chatId}/read/${userId}`, (payload) => {
        messageStore.readMessage(
            chatId,
            payload.messageId,
        )
      }),
      subscribe(`/topic/chat/${chatId}/status`, (payload) => {
        if (activeChatName.value === payload.nickname) {
          interlocutorStatus.value = payload.status
        }
      })
  )
}

const handleReturnToList = () => {
  activeChatSubscriptions.value.forEach(unsub => unsub())
  activeChatSubscriptions.value = []

  activeChatName.value = ''
  activeChat.value = null
}

const handleSearchUser = (interlocutorUsername, interlocutorNickname) => {
  if (!interlocutorUsername?.trim()) {
    return
  }

  interlocutor.value = interlocutorUsername
  console.log("ok")

  const existingChat = chats.value.find(chat =>
      chat?.participantInfo?.some(p => p.username === interlocutorUsername)
  )

  if (existingChat) {
    activeChat.value = existingChat
  } else {
    activeChat.value = null
  }
  activeChatName.value = interlocutorNickname
}

const sendMessage = async (content) => {
  const trimmedContent = content?.trim()
  if (!trimmedContent) return

  const userId = localStorage.getItem('userId')
  const username = localStorage.getItem('username')

  if (!activeChat.value && activeChatName.value) {
    activeChat.value = await createChat({
      type: 'DIRECT',
      title: null,
      participants: [username, interlocutor.value]
    })

    if (activeChat.value?.id) {
      subscribeToChat(activeChat.value.id, handleMessage)

      subscribe(`/topic/chat/${activeChat.value.id}/edited`, (payload) => {
        messageStore.updateMessage(activeChat.value.id, payload.messageId, payload.newContent)
      })
      subscribe(`/topic/chat/${activeChat.value.id}/deleted`, (payload) => {
        messageStore.removeMessage(activeChat.value.id, payload.messageId)
      })
      subscribe(`/topic/chat/${activeChat.value.id}/read/${userId}`, (payload) => {
        messageStore.readMessage(
            activeChat.value.id,
            payload.messageId,
        )
      })
      subscribe(`/topic/chat/${activeChat.value.id}/status`, (payload) => {
        console.log(payload.status)
        console.log(payload.username)
        if (activeChatName.value === payload.nickname) {
            interlocutorStatus.value = payload.status
        }
      })
    }
  }

  if (!activeChat.value?.id) return

  const messagePayload = {
    chatId: activeChat.value.id,
    senderId: userId,
    content: trimmedContent,
    sentAt: new Date().toISOString().slice(0, -1)
  }

  send('/bp-min/chat.sendMessage', messagePayload)
}

const handleEditMessage = ({ id, content }) => {
  send('/bp-min/chat.editMessage', {
    messageId: id,
    chatId: activeChat.value.id,
    newContent: content
  })
}

const handleDeleteMessage = (messageId) => {
  if (!activeChat.value?.id) return

  const payload = {
    messageId: messageId,
    chatId: activeChat.value.id
  }

  send('/bp-min/chat.deleteMessage', payload)
}

</script>

<template>
  <div class="sideBar">
    <div class="searchBar">
      <SearchBar @user-selected="handleSearchUser" />
    </div>
    <div class="chatListWrapper">
      <div v-if="loading" class="spinner-container">
        <div class="spinner"></div>
      </div>

      <div v-else-if="error" class="error-message">
        <p>Failed to load chats</p>
        <button @click="fetchChats" class="retry-button">Try again</button>
      </div>

      <ChatList
          v-else
          :chats="chats"
          @chat-selected="handleChatSelected"
          :current-chat-id="activeChat?.id"
      />
    </div>

    <div class="sidebar-footer">
      <button
          class="profile-avatar-button"
          @click="router.push(profilePath)"
          aria-label="Profile"
      >
        <div class="profile-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
        </div>
        <span>Your profile</span>
      </button>
    </div>
  </div>

  <main>
    <ConversationView
        :current-conversation-name="activeChatName"
        :current-conversation="activeChat"
        :current-interlocutor="interlocutor"
        :interlocutor-status="interlocutorStatus"
        @return-to-list="handleReturnToList"
        @send-message="sendMessage"
        @delete-message="handleDeleteMessage"
        @edit-message="handleEditMessage"
        @message-read="handleMessageRead"
    />
  </main>
</template>

<style scoped>
/* ────────────────────────────────────
   Sidebar footer with profile button
   ──────────────────────────────────── */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  background: white;
  position: sticky;
  bottom: 0;
  z-index: 20;
}

.profile-avatar-button {
  width: 100%;
  padding: 12px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%);
  color: white;
  font-weight: 600;
  font-size: 14px;
  border: none;
  cursor: pointer;
  box-shadow:
      0 4px 12px rgba(126, 74, 255, 0.3),
      0 2px 6px rgba(249, 115, 22, 0.2);
  transition: all 0.3s ease;
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
  display: flex;
  align-items: center;
  gap: 12px;
  text-align: left;
}

.profile-avatar-button:hover {
  transform: translateY(-2px);
  box-shadow:
      0 6px 20px rgba(126, 74, 255, 0.4),
      0 4px 12px rgba(249, 115, 22, 0.3);
}

.profile-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.profile-avatar-button:hover .profile-icon {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.profile-avatar-button span {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ────────────────────────────────────
   Global font settings
   ──────────────────────────────────── */
.sideBar,
main {
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
}

/* ────────────────────────────────────
   Global layout styles
   ──────────────────────────────────── */
:global(html),
:global(body) {
  margin: 0;
  padding: 0;
  height: 100%;
}

:global(#app) {
  min-height: 100vh;
  background: linear-gradient(135deg, #7e4aff, #6a2c91);
  display: flex;
  overflow: hidden;
}

/* ────────────────────────────────────
   Sidebar container
   ──────────────────────────────────── */
.sideBar {
  width: 320px;
  height: 100vh;
  background: white;
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

/* ────────────────────────────────────
   Search bar section
   ──────────────────────────────────── */
.searchBar {
  padding: 16px 16px 12px;
  position: sticky;
  top: 0;
  background: white;
  z-index: 20;
}

/* ────────────────────────────────────
   Chat list container and scrolling
   ──────────────────────────────────── */
.chatListWrapper {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px 16px;
}

/* Custom scrollbar styles */
.chatListWrapper::-webkit-scrollbar {
  width: 6px;
}

.chatListWrapper::-webkit-scrollbar-track {
  background: transparent;
}

.chatListWrapper::-webkit-scrollbar-thumb {
  background: #c5c5c5;
  border-radius: 10px;
}

.chatListWrapper::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* ────────────────────────────────────
   Main chat view area
   ──────────────────────────────────── */
main {
  flex: 1;
  height: 100vh;
  background: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #666;
}

/* ────────────────────────────────────
   Loading and error states
   ──────────────────────────────────── */
.spinner-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 32px 0;
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #f0f0f0;
  border-top: 3px solid #7e4aff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  padding: 40px 20px;
  color: #d32f2f;
}

.error-message p {
  margin-bottom: 16px;
  font-size: 16px;
}

.retry-button {
  background: #7e4aff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.retry-button:hover {
  background: #6a2c91;
}
</style>