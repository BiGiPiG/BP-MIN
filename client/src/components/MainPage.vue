<script setup>
import { onMounted, ref, watch } from 'vue'
import ChatList from '@/components/ChatList.vue'
import ChatView from '@/components/ChatView.vue'
import SearchBar from '@/components/SearchBar.vue'
import { useChats } from "@/utils/useChats.js"
import { useStomp } from "@/utils/useStomp.js";
import { useMessageStore } from "@/utils/useMessages.js";

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

onMounted(async () => {
  await fetchChats();

  const token = localStorage.getItem('accessToken');
  if (!token) {
    router.push({ name: 'Login' });
    return;
  }

  const connectHeaders = { Authorization: `Bearer ${token}` };

  try {
    await connect("ws://localhost:8080/chats", connectHeaders, () => {
      console.log('🔍 Пытаюсь подписаться на /user/queue/new-chat');
      subscribe(`/topic/user/${localStorage.getItem('userId')}/chats`, (newChat) => {
        console.log('🆕 Получен новый чат:', newChat);
        const exists = chats.value.some(chat => chat.id === newChat.id);
        if (!exists) {
          chats.value.unshift(newChat);
        }
      });

      const chatList = Array.isArray(chats.value) ? chats.value : [];
      if (chatList.length > 0) {
        subscribeToAllChats(chatList, (message) => {
          console.log('📩 Новое сообщение:', message);
          const parsedMessage = typeof message === 'string' ? JSON.parse(message) : message;
          messageStore.addMessage(parsedMessage.chatId, parsedMessage);
        });
      }
    });
  } catch (error) {
    console.error('❌ WebSocket connection failed:', error);
    router.push({ name: 'Login' });
  }
});

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

const sendMessage = async (content) => {
  const userId = localStorage.getItem('userId');
  const username = localStorage.getItem('username');

  if (activeChat.value == null && activeChatName.value != null) {
    console.log("===new Chat===")
    activeChat.value = await createChat({
      type: 'DIRECT',
      title: null,
      participants: [username, activeChatName.value]
    })
    subscribeToChat(activeChat.value.id, (message) => {
      console.log('📩 Новое сообщение:', message);
      messageStore.addMessage(message.chatId, message);
    });
  }

  if (!activeChat.value?.id || !content?.trim()) {
    console.warn('Нельзя отправить пустое сообщение или нет активного чата')
    return
  }

  const messagePayload = {
    chatId: activeChat.value.id,
    senderId: userId,
    content: content.trim(),
    timestamp: new Date().toISOString()
  }

  send('/app/chat', messagePayload)
}

const handleSearchUser = (chatName) => {
  const existingChat = chats.value.find(chat =>
      chat != null &&
      Array.isArray(chat.participantInfo) &&
      chat.participantInfo.some(p => p.nickname === chatName)
  );

  if (existingChat) {
    activeChat.value = existingChat;
    activeChatName.value = chatName;
    return;
  }

  activeChatName.value = chatName;
  activeChat.value = null;
}

import { onUnmounted } from 'vue'
onUnmounted(() => {
  unsubscribeFromAllChats()
  disconnect()
})
</script>

<template>
  <div class="sideBar">
    <div class="searchBar">
      <SearchBar
        @user-selected="handleSearchUser"
      />
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
        @send-message="sendMessage"
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