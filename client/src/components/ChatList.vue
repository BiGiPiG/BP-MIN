<script setup>
import { computed } from 'vue'

// ─────────────────────────────────────
// Props & Emits
// ─────────────────────────────────────

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

const emit = defineEmits(['chat-selected'])

// ─────────────────────────────────────
// Constants
// ─────────────────────────────────────

const GRADIENTS = [
  'linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%)',
  'linear-gradient(135deg, #8b5cf6 0%, #c4b5fd 100%)',
  'linear-gradient(135deg, #a855f7 0%, #d8b4fe 100%)',
  'linear-gradient(135deg, #d946ef 0%, #f0abfc 100%)',
  'linear-gradient(135deg, #f97316 0%, #fdba74 100%)',
  'linear-gradient(135deg, #ea580c 0%, #fed7aa 100%)',
  'linear-gradient(135deg, #7e4aff 0%, #f97316 100%)',
  'linear-gradient(135deg, #a855f7 0%, #ea580c 100%)'
]

// ─────────────────────────────────────
// Computed Properties
// ─────────────────────────────────────

const chatGradients = computed(() => {
  return props.chats?.reduce((acc, chat) => {
    acc[chat.id] = GRADIENTS[Math.floor(Math.random() * GRADIENTS.length)]
    return acc
  }, {}) || {}
})

const processedChats = computed(() => {
  const currentUsername = localStorage.getItem('username') || ''

  return props.chats.map(chat => {
    // Get interlocutor name
    let displayName = 'Unknown Chat'
    let initial = '?'

    if (chat?.participantInfo?.length) {
      const interlocutor = chat.participantInfo.find(
          participant => participant.username !== currentUsername
      )

      if (interlocutor?.nickname) {
        displayName = interlocutor.nickname
        initial = interlocutor.nickname.charAt(0).toUpperCase() || '?'
      }
    }

    return {
      ...chat,
      displayName,
      initial,
      isActive: props.currentChatId === chat.id
    }
  })
})

// ─────────────────────────────────────
// Methods
// ─────────────────────────────────────

const switchActive = (chat) => {
  emit('chat-selected', chat, chat.displayName)
}
</script>

<template>
  <div class="chat-list">
    <div
        v-for="chat in processedChats"
        :key="chat.id"
        class="chat-item"
        :class="{ 'active': chat.isActive }"
        @click="switchActive(chat)"
    >
      <div
          class="avatar"
          :style="{ background: chatGradients[chat.id] }"
      >
        {{ chat.initial }}
      </div>

      <div class="chat-content">
        <div class="title-row">
          <div class="title">{{ chat.displayName }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ────────────────────────────────────
   Chat List Container
   ──────────────────────────────────── */
.chat-list {
  background: transparent;
}

/* ────────────────────────────────────
   Chat Item
   ──────────────────────────────────── */
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

/* ────────────────────────────────────
   Avatar
   ──────────────────────────────────── */
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

/* ────────────────────────────────────
   Chat Content
   ──────────────────────────────────── */
.chat-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  position: relative;
  z-index: 2;
}

.title-row {
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

/* ────────────────────────────────────
   Animations
   ──────────────────────────────────── */
.chat-item {
  animation: slide-in 0.4s ease-out;
}

@keyframes slide-in {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* ────────────────────────────────────
   Responsive Design
   ──────────────────────────────────── */
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
}
</style>