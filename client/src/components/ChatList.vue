<script setup>
import { computed } from 'vue'

const emit = defineEmits(['chat-selected'])

const props = defineProps({
  chats: {
    type: Array,
    required: true,
    default: () => []
  },
  currentChatId: {
    type: [String, Number, null],
    default: null
  }
})

const getRandomGradient = () => {
  const gradients = [
    'linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%)',
    'linear-gradient(135deg, #8b5cf6 0%, #c4b5fd 100%)',
    'linear-gradient(135deg, #a855f7 0%, #d8b4fe 100%)',
    'linear-gradient(135deg, #d946ef 0%, #f0abfc 100%)',
    'linear-gradient(135deg, #f97316 0%, #fdba74 100%)',
    'linear-gradient(135deg, #ea580c 0%, #fed7aa 100%)',
    'linear-gradient(135deg, #7e4aff 0%, #f97316 100%)',
    'linear-gradient(135deg, #a855f7 0%, #ea580c 100%)'
  ]
  return gradients[Math.floor(Math.random() * gradients.length)]
}

const chatGradients = computed(() => {
  return props.chats?.reduce((acc, chat) => {
    acc[chat.id] = getRandomGradient()
    return acc
  }, {}) || {}
})

function getChatname(chat) {
  const user = localStorage.getItem('username')
  if (!chat.participantInfo) return 'Unknown Chat'

  const interlocutor = chat.participantInfo.find(participant =>
      participant.nickname !== user
  )
  return interlocutor?.nickname || 'Unknown User'
}

function getChatInitial(chat) {
  const name = getChatname(chat)
  return name?.charAt(0)?.toUpperCase() || '?'
}

function switchActive(chat) {
  const chatName = getChatname(chat)
  emit('chat-selected', chat, chatName)
}

const processedChats = computed(() => {
  return props.chats.map(chat => ({
    ...chat,
    displayName: getChatname(chat),
    initial: getChatInitial(chat),
    isActive: props.currentChatId === chat.id
  }))
})
</script>

<template>
  <div class="chat-list">
    <div
        v-for="chat in processedChats"
        :key="chat.id"
        class="chat-item"
        :class="{
          'active': chat.isActive
        }"
        @click="switchActive(chat)"
    >
      <div
          class="avatar"
          :style="{ background: chatGradients[chat.id] }"
      >
        {{ chat.initial }}
      </div>
      <div class="chatContent">
        <div class="titleRow">
          <div class="title">{{ chat.displayName }}</div>
          <div v-if="chat.unread" class="unreadBadge"></div>
        </div>
        <div class="lastMessage">{{ chat.lastMessagePreview }}</div>
        <div class="chatMeta">
          <div class="time">{{ chat.lastActivity }}</div>
          <div v-if="chat.unreadCount" class="unreadCount">{{ chat.unreadCount }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-list {
  background: transparent;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f8f7ff;
  background: white;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.chat-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 0;
  background: linear-gradient(90deg, #7e4aff, #f97316);
  transition: width 0.3s ease;
}

.chat-item:hover::before {
  width: 4px;
}

.chat-item:hover {
  background: linear-gradient(135deg, #faf8ff, #fff7ed);
  transform: translateX(4px);
  border-bottom-color: #ede9fe;
}

.chat-item.unread {
  background: linear-gradient(135deg, #f5f3ff, #fffbeb);
}

.chat-item.unread .title {
  color: #7e4aff;
  font-weight: 700;
}

.chat-item.unread .lastMessage {
  color: #a855f7;
  font-weight: 500;
}

.chat-item.active {
  background: linear-gradient(135deg, #f0ebff, #fff7ed);
  border-left: 4px solid #7e4aff;
  padding-left: 16px;
}

.chat-item.active::before {
  display: none;
}

.chat-item.active .title {
  color: #7e4aff;
  font-weight: 700;
}

.chat-item.active .lastMessage {
  color: #8b5cf6;
}

.avatar {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 20px;
  margin-right: 16px;
  flex-shrink: 0;
  box-shadow:
      0 4px 12px rgba(126, 74, 255, 0.3),
      0 2px 6px rgba(249, 115, 22, 0.2);
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.chat-item:hover .avatar {
  transform: scale(1.05);
  box-shadow:
      0 6px 20px rgba(126, 74, 255, 0.4),
      0 4px 12px rgba(249, 115, 22, 0.3);
}

.chatContent {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
  z-index: 2;
}

.titleRow {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #1f1f1f;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.lastMessage {
  font-size: 14px;
  color: #6b7280;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.chatMeta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: 2px;
}

.time {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

.unreadBadge {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f97316, #ea580c);
  box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.2);
  flex-shrink: 0;
}

.unreadCount {
  background: linear-gradient(135deg, #7e4aff, #f97316);
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 12px;
  min-width: 18px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(126, 74, 255, 0.3);
}

.chat-item {
  animation: slideIn 0.4s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 768px) {
  .chat-item {
    padding: 14px 16px;
  }

  .avatar {
    width: 48px;
    height: 48px;
    font-size: 18px;
    margin-right: 14px;
  }

  .title {
    font-size: 15px;
  }

  .lastMessage {
    font-size: 13px;
  }
}
</style>