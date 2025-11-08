<script setup>
import { ref, defineEmits, onMounted, computed, watch } from 'vue'
import { useMessageStore } from '@/utils/useMessages'

const emit = defineEmits(['return-to-list', 'send-message'])
const messageText = ref('')
const currentUserId = Number(localStorage.getItem('userId'));
const floatingElements = ref([])

const messageStore = useMessageStore()

const props = defineProps({
  currentConversationName: String,
  currentConversation: [Object, null]
})

const conversationHistory = computed(() => {
  return props.currentConversation?.id
      ? messageStore.getMessages(props.currentConversation.id)
      : []
})

watch(() => props.currentConversation?.id, (chatId) => {
  if (chatId) {
    messageStore.loadMessages(chatId)
  }
})

const handleSendMessage = () => {
  const text = messageText.value.trim()
  if (text && props.currentConversationName) {
    emit('send-message', text)
    messageText.value = ''
  }
}

onMounted(() => {
  console.log(props.currentConversation);
  const elementsArray = []
  for (let i = 0; i < 25; i++) {
    elementsArray.push({
      id: i,
      positionY: Math.random() * 100,
      positionX: Math.random() * 100,
      dimension: 8 + (i % 12),
      startDelay: (i % 5) * 0.8,
      animationLength: 3 + (i % 4)
    })
  }
  floatingElements.value = elementsArray
})
</script>

<template>
  <div v-if="!props.currentConversationName" class="conversation-placeholder">
    <div class="animated-background">
      <div
          v-for="element in floatingElements"
          :key="element.id"
          class="floating-dot"
          :style="{
          top: `${element.positionY}%`,
          left: `${element.positionX}%`,
          width: `${element.dimension}px`,
          height: `${element.dimension}px`,
          animationDelay: `${element.startDelay}s`,
          animationDuration: `${element.animationLength}s`
        }"
      ></div>
    </div>

    <div class="placeholder-content">
      <div class="placeholder-card">
        <h2>Select a conversation to start chatting</h2>
      </div>
    </div>
  </div>

  <div v-else class="conversation-interface">
    <div class="conversation-header">
      <button @click="emit('return-to-list')" class="navigation-button">←</button>
      <div class="contact-name">{{ currentConversationName }}</div>
    </div>

    <div class="messages-container">
      <div v-for="(message, idx) in conversationHistory" :key="idx" class="message-container">
        <div :class="message.senderId === currentUserId ? 'message-bubble outgoing' : 'message-bubble incoming'">
          <div class="message-content">{{ message.content }}</div>
          <div class="message-meta">{{ message.sentAt }}</div>
        </div>
      </div>
    </div>

    <div class="message-composer">
      <input
          v-model="messageText"
          @keyup.enter="handleSendMessage"
          type="text"
          placeholder="Type your message..."
          class="composer-input"
      />
      <button @click="handleSendMessage" class="send-button">Send</button>
    </div>
  </div>
</template>

<style scoped>
.conversation-placeholder {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea, #764ba2);
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.animated-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  overflow: hidden;
}

.floating-dot {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: gentle-float 5s infinite ease-in-out;
  z-index: 2;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.6);
}

@keyframes gentle-float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.6;
  }
  25% {
    transform: translate(12px, -18px) scale(1.15);
    opacity: 1;
  }
  50% {
    transform: translate(-8px, 12px) scale(0.85);
    opacity: 0.7;
  }
  75% {
    transform: translate(18px, 8px) scale(1.08);
    opacity: 0.9;
  }
}

.placeholder-content {
  position: relative;
  z-index: 3;
  text-align: center;
  color: white;
  padding: 0 24px;
}

.placeholder-card {
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(16px);
  border-radius: 24px;
  padding: 40px 32px;
  max-width: 520px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.placeholder-card h2 {
  font-size: 26px;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.conversation-interface {
  background: linear-gradient(135deg, #f8faff 0%, #fef7ed 100%);
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;
  position: relative;
}

.conversation-interface::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, #667eea, #f093fb, transparent);
  z-index: 2;
}

.conversation-header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(102, 126, 234, 0.15);
  box-shadow: 0 2px 24px rgba(102, 126, 234, 0.12);
  height: 68px;
  z-index: 10;
}

.navigation-button {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: white;
  margin-right: 20px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35);
}

.navigation-button:hover {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 24px rgba(245, 87, 108, 0.45);
}

.contact-name {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #f093fb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.messages-container {
  flex: 1;
  padding: 28px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background:
      radial-gradient(circle at 15% 75%, rgba(102, 126, 234, 0.08) 0%, transparent 50%),
      radial-gradient(circle at 85% 25%, rgba(240, 147, 251, 0.08) 0%, transparent 50%);
}

.message-container {
  display: flex;
  width: 100%;
}

.message-container .outgoing {
  margin-left: auto;
  justify-content: flex-end;
}

.message-container .incoming {
  margin-right: auto;
  justify-content: flex-start;
}

.message-bubble {
  max-width: 65%;
  padding: 18px 22px;
  border-radius: 22px;
  position: relative;
  word-break: break-word;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
}

.message-bubble:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.18);
}

.message-bubble.incoming {
  background: linear-gradient(135deg, #ffffff, #f8faff);
  border: 1px solid rgba(102, 126, 234, 0.15);
  border-bottom-left-radius: 8px;
  border-top-left-radius: 8px;
}

.message-bubble.incoming::before {
  content: '';
  position: absolute;
  left: -10px;
  top: 0;
  width: 10px;
  height: 100%;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 6px 0 0 6px;
}

.message-bubble.outgoing {
  background: linear-gradient(135deg, #667eea, #f093fb);
  color: white;
  border-bottom-right-radius: 8px;
  border-top-right-radius: 8px;
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.35);
}

.message-bubble.outgoing::before {
  content: '';
  position: absolute;
  right: -10px;
  top: 0;
  width: 10px;
  height: 100%;
  background: linear-gradient(180deg, #f093fb, #f5576c);
  border-radius: 0 6px 6px 0;
}

.message-content {
  font-size: 16px;
  line-height: 1.5;
  margin-bottom: 6px;
}

.message-meta {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 8px;
  text-align: right;
  font-weight: 600;
}

.message-bubble.incoming .message-meta {
  color: #667eea;
}

.message-bubble.outgoing .message-meta {
  color: rgba(255, 255, 255, 0.95);
}

.message-composer {
  display: flex;
  padding: 24px 28px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(12px);
  border-top: 1px solid rgba(102, 126, 234, 0.15);
  gap: 16px;
}

.composer-input {
  flex: 1;
  padding: 18px 22px;
  border: 2px solid rgba(102, 126, 234, 0.25);
  border-radius: 18px;
  outline: none;
  font-size: 16px;
  background: rgba(248, 250, 255, 0.9);
  transition: all 0.3s ease;
  color: #333;
}

.composer-input:focus {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 5px rgba(102, 126, 234, 0.2);
}

.composer-input::placeholder {
  color: #a3b1f1;
  font-weight: 500;
}

.send-button {
  background: linear-gradient(135deg, #667eea, #f093fb);
  color: white;
  border: none;
  border-radius: 18px;
  padding: 18px 28px;
  cursor: pointer;
  font-weight: 700;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
  min-width: 90px;
}

.send-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.45);
  background: linear-gradient(135deg, #f093fb, #667eea);
}

/* Стили для скроллбара */
.messages-container::-webkit-scrollbar {
  width: 8px;
}

.messages-container::-webkit-scrollbar-track {
  background: rgba(102, 126, 234, 0.08);
  border-radius: 4px;
}

.messages-container::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea, #f093fb);
  border-radius: 4px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #f093fb, #667eea);
}

/* Анимации */
.message-bubble {
  animation: bubble-appear 0.4s ease-out;
}

@keyframes bubble-appear {
  from {
    opacity: 0;
    transform: translateY(15px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .conversation-interface {
    border-radius: 0;
  }

  .messages-container {
    padding: 20px 16px;
    gap: 16px;
  }

  .message-composer {
    padding: 20px 16px;
  }

  .message-bubble {
    max-width: 80%;
    padding: 16px 20px;
  }

  .conversation-header {
    padding: 0 16px;
    height: 60px;
  }

  .navigation-button {
    width: 40px;
    height: 40px;
    margin-right: 16px;
  }

  .placeholder-card {
    padding: 32px 24px;
    margin: 0 16px;
  }

  .placeholder-card h2 {
    font-size: 22px;
  }
}
</style>