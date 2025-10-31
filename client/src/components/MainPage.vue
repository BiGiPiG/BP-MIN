<script setup>
import { onMounted, ref, computed } from 'vue'
import ChatList from '@/components/ChatList.vue'
import ChatView from '@/components/ChatView.vue'
import SearchBar from '@/components/SearchBar.vue'
import { useChats } from "@/utils/useChats.js"

const { chats, loading, error, fetchChats } = useChats()

onMounted(() => {
  fetchChats()
})

const activeChatName = ref('')
const activeChat = ref(null)

const handleChatSelected = (chat, chatName) => {
  activeChatName.value = chatName
  activeChat.value = chat
}

const handleReturnToList = () => {
  activeChatName.value = ''
  activeChat.value = null
}

</script>

<template>
  <div class="sideBar">
    <div class="searchBar">
      <SearchBar />
    </div>
    <div class="chatListWrapper">
      <div v-if="loading" class="spinner-container">
        <div class="spinner"></div>
      </div>

      <div v-else-if="error" class="error-message">
        Не удалось загрузить чаты. Попробуйте позже.
      </div>

      <ChatList
          v-else
          :chats="chats"
          @chat-selected="handleChatSelected"
          :currentChatId="activeChat?.id"
      />
    </div>
  </div>
  <main>
    <ChatView
        :currentConversationName="activeChatName"
        :currentConversation="activeChat"
        @return-to-list="handleReturnToList"
    />
  </main>
</template>

<style scoped>
.sideBar,
main {
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
}

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

.searchBar {
  padding: 16px 16px 12px;
  position: sticky;
  top: 0;
  background: white;
  z-index: 20;
}

.searchBar::before {
  content: '🔍';
  position: absolute;
  left: 28px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 16px;
  pointer-events: none;
}

.searchBar input,
.searchBar .search-input {
  width: 100%;
  padding: 12px 16px 12px 40px;
  border: 1.5px solid #e8e8e8;
  border-radius: 16px;
  font-size: 15px;
  outline: none;
  background: #f8f9fa;
  color: #333;
  font-weight: 500;
  box-sizing: border-box;
  transition: all 0.25s ease;
}

.searchBar input:focus,
.searchBar .search-input:focus {
  border-color: #7e4aff;
  background: white;
  box-shadow: 0 0 0 4px rgba(126, 74, 255, 0.15);
}

.chatListWrapper {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px 16px;
}

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

:global(body),
:global(html) {
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

main {
  flex: 1;
  height: 100vh;
  background: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #666;
}

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
</style>