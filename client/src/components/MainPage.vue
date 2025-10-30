<script setup>
import { onMounted, ref, computed } from 'vue'
import ChatList from '@/components/ChatList.vue'
import ChatView from '@/components/ChatView.vue'
import SearchBar from '@/components/SearchBar.vue'
import { useChats } from "@/utils/useChats.js"

let chats = [
  {
    id: 1,
    type: 'DIRECT',
    title: null,
    lastMessagePreview: 'Hi, bro)',
    unread: true,
    participants: [
      {nick: 'userok', id: 1},
      {nick: 'BiGPiG', id: 2}
    ]
  },
  {
    id: 2,
    type: 'DIRECT',
    title: null,
    lastMessagePreview: 'Call me later',
    unread: true,
    participants: [
      {nick: 'userok', id: 1},
      {nick: 'Ma', id: 2}
    ]
  }
]

const activeChatName = ref('')
const activeChat = ref(null)

const handleChatSelected = (chat, chatName) => {
  activeChatName.value = chatName
  activeChat.value = chat
}

</script>

<template>
  <div class="sideBar">
    <div class="SearchBar">
      <searchBar/>
    </div>
    <div class="chatListWrapper">
      <ChatList
        :chats="chats"
        @chat-selected="handleChatSelected"
      />
    </div>
  </div>
  <main>
      <ChatView
          :chatName="activeChatName"
          :chat="activeChat"
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
</style>