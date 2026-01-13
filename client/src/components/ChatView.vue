<script setup>
import { ref, defineEmits, defineProps, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { useMessageStore } from '@/utils/useMessages'

// ─────────────────────────────────────
// Props & Emits
// ─────────────────────────────────────

const props = defineProps({
  currentConversationName: String,
  currentConversation: [Object, null]
})

const emit = defineEmits(['return-to-list', 'send-message', 'delete-message'])

// ─────────────────────────────────────
// Reactive State
// ─────────────────────────────────────

const isProfileVisible = ref(false)
const messageText = ref('')
const messagesContainer = ref(null)
const currentUserId = Number(localStorage.getItem('userId'))

// Context menu state
const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  messageId: null
})

// Scrollbar visibility
const isScrollbarVisible = ref(true)
let scrollActivityTimeout = null

// ─────────────────────────────────────
// Computed Properties
// ─────────────────────────────────────

const profileData = computed(() => ({
  username: localStorage.getItem('username') || 'User',
  userId: localStorage.getItem('userId') || '',
  bio: localStorage.getItem('userBio') || 'No bio provided.',
  birthDate: localStorage.getItem('userBirthDate') || ''
}))

const profileInitial = computed(() => {
  return profileData.value.username.charAt(0).toUpperCase() || '?'
})

const conversationHistory = computed(() => {
  return props.currentConversation?.id
      ? messageStore.getMessages(props.currentConversation.id)
      : []
})

const isScrollLocked = computed(() => contextMenu.value.visible)

// ─────────────────────────────────────
// Composable
// ─────────────────────────────────────

const messageStore = useMessageStore()

// ─────────────────────────────────────
// Lifecycle Hooks
// ─────────────────────────────────────

onMounted(() => {
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

  nextTick(() => {
    scrollToBottom()
  })

  document.addEventListener('click', hideContextMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', hideContextMenu)
  if (scrollActivityTimeout) {
    clearTimeout(scrollActivityTimeout)
  }
})

// ─────────────────────────────────────
// Watchers
// ─────────────────────────────────────

watch(() => props.currentConversation?.id, (chatId) => {
  if (chatId) {
    messageStore.loadMessages(chatId)
  }
})

watch(conversationHistory, () => {
  nextTick(() => {
    scrollToBottom()
  })
}, { deep: true })

// ─────────────────────────────────────
// Methods
// ─────────────────────────────────────

// Profile
const openProfile = () => isProfileVisible.value = true
const closeProfile = () => isProfileVisible.value = false

// Message input
const handleSendMessage = () => {
  const text = messageText.value.trim()
  if (text && props.currentConversationName) {
    emit('send-message', text)
    messageText.value = ''
  }
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// Tooltip
const hoveredMessageId = ref(null)
const showTooltip = (id) => hoveredMessageId.value = id
const hideTooltip = () => hoveredMessageId.value = null

// Scrollbar activity
const showScrollbar = () => {
  isScrollbarVisible.value = true
  if (scrollActivityTimeout) {
    clearTimeout(scrollActivityTimeout)
  }
  scrollActivityTimeout = setTimeout(() => {
    isScrollbarVisible.value = false
  }, 1500)
}

// Context Menu
const preventDefaultContextMenu = (e) => {
  e.preventDefault()
}

const showContextMenu = (e, message) => {
  e.preventDefault()
  if (String(message.senderId) !== String(currentUserId)) return

  // Ensure menu stays in viewport
  const x = Math.max(e.clientX, 140)
  const y = Math.max(e.clientY, 60)

  contextMenu.value = {
    visible: true,
    x,
    y,
    messageId: message.id
  }
}

const hideContextMenu = () => {
  contextMenu.value.visible = false
}

const handleEditMessage = () => {
  console.log('Edit message:', contextMenu.value.messageId)
  hideContextMenu()
}

const handleDeleteMessage = () => {
  emit('delete-message', contextMenu.value.messageId)
  hideContextMenu()
}

// Floating background (for placeholder)
const floatingElements = ref([])
</script>

<template>
  <!-- Placeholder state -->
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

  <!-- Active conversation -->
  <div v-else class="conversation-interface">
    <div class="conversation-header">
      <button @click="emit('return-to-list')" class="navigation-button">←</button>
      <div class="contact-name" @click="openProfile">{{ currentConversationName }}</div>
    </div>

    <div
        ref="messagesContainer"
        class="messages-container"
        :class="{ 'scroll-locked': isScrollLocked, 'scrollbar-visible': isScrollbarVisible }"
        @contextmenu="preventDefaultContextMenu"
        @scroll="showScrollbar"
        @mouseenter="showScrollbar"
        @mouseleave="showScrollbar"
    >
      <div
          v-for="message in conversationHistory"
          :key="message.id"
          class="message-container"
      >
        <div
            :class="
            String(message.senderId) === String(currentUserId)
              ? 'message-bubble outgoing'
              : 'message-bubble incoming'
          "
            @contextmenu="(e) => showContextMenu(e, message)"
        >
          <div class="message-content">{{ message.content }}</div>
          <div class="message-meta-wrapper">
            <div
                class="message-meta"
                @mouseenter="() => showTooltip(message.id)"
                @mouseleave="hideTooltip"
            >
              {{ message.shortSentAt }}
              <div
                  v-if="hoveredMessageId === message.id"
                  class="custom-tooltip"
              >
                {{ message.fullSentAt }}
              </div>
            </div>
          </div>
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

    <!-- Profile modal -->
    <teleport to="body" v-if="isProfileVisible">
      <div class="profile-overlay" @click="closeProfile">
        <div class="profile-modal" @click.stop>
          <div class="profile-header">
            <button class="close-button" @click="closeProfile">×</button>
            <h2>User Profile</h2>
          </div>

          <div class="profile-avatar-section">
            <div
                class="profile-avatar"
                :style="{ background: 'linear-gradient(135deg, #667eea, #764ba2)' }"
            >
              {{ profileInitial }}
            </div>
          </div>

          <div class="profile-info">
            <div class="profile-field">
              <label>Username</label>
              <div>{{ profileData.username }}</div>
            </div>
            <div class="profile-field">
              <label>User ID</label>
              <div>{{ profileData.userId }}</div>
            </div>
            <div class="profile-field">
              <label>Birth Date</label>
              <div>{{ profileData.birthDate || '—' }}</div>
            </div>
            <div class="profile-field">
              <label>About</label>
              <div class="bio-text">{{ profileData.bio }}</div>
            </div>
          </div>
        </div>
      </div>
    </teleport>

    <!-- Context Menu -->
    <teleport to="body" v-if="contextMenu.visible">
      <div
          class="context-menu"
          :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
          @click.stop
      >
        <button @click="handleEditMessage" class="context-menu-item">Edit</button>
        <button @click="handleDeleteMessage" class="context-menu-item delete">Delete</button>
      </div>
    </teleport>
  </div>
</template>

<style scoped>
/* ────────────────────────────────────
   CONTEXT MENU STYLES
   ──────────────────────────────────── */
.context-menu {
  position: fixed;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(102, 126, 234, 0.2);
  z-index: 3000;
  min-width: 140px;
  overflow: hidden;
  font-family: 'Segoe UI', system-ui, sans-serif;
  user-select: none;
  transform: translate(-100%, -100%); /* Правый нижний угол — под курсором */
  max-width: calc(100vw - 20px);
  max-height: calc(100vh - 20px);
}

.context-menu-item {
  width: 100%;
  padding: 12px 16px;
  background: none;
  border: none;
  text-align: left;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  color: #333;
}

.context-menu-item:hover {
  background: rgba(102, 126, 234, 0.1);
}

.context-menu-item.delete {
  color: #f5576c;
}

.context-menu-item.delete:hover {
  background: rgba(245, 87, 108, 0.1);
}

/* ────────────────────────────────────
   SCROLLBAR: auto-hide, no layout shift
   ──────────────────────────────────── */
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
  scrollbar-width: thin;
  -ms-overflow-style: -ms-autohiding-scrollbar;
}

.messages-container::-webkit-scrollbar {
  width: 8px;
}

.messages-container::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 4px;
}

.messages-container::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea, #f093fb);
  border-radius: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.messages-container.scrollbar-visible::-webkit-scrollbar-thumb {
  opacity: 1;
}

.messages-container:not(.scrollbar-visible) {
  scrollbar-color: transparent transparent;
}

.messages-container.scrollbar-visible {
  scrollbar-color: #667eea rgba(102, 126, 234, 0.08);
}

.messages-container.scroll-locked {
  overflow: hidden;
}

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
  cursor: pointer;
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
  min-width: 50px;
  padding: 18px 22px;
  border-radius: 22px;
  position: relative;
  word-break: break-word;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
  overflow: visible;
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

.profile-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100dvh;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(6px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  animation: modal-fade-in 0.25s ease-out;
}

@keyframes modal-fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

.profile-modal {
  width: 90%;
  max-width: 500px;
  background: white;
  border-radius: 24px;
  padding: 32px;
  box-shadow:
      0 20px 50px rgba(0, 0, 0, 0.25),
      0 10px 20px rgba(102, 126, 234, 0.15);
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
  max-height: 85dvh;
  overflow-y: auto;
  font-family: 'Segoe UI', system-ui, sans-serif;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.close-button {
  background: none;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: #667eea;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.2s;
}

.close-button:hover {
  background: rgba(102, 126, 234, 0.1);
}

.profile-header h2 {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #f093fb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.profile-avatar-section {
  display: flex;
  justify-content: center;
  margin: 16px 0 28px;
}

.profile-avatar {
  width: 100px;
  height: 100px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 40px;
  font-weight: 700;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.profile-field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #7e88a0;
  margin-bottom: 6px;
}

.profile-field div {
  font-size: 16px;
  color: #222;
  word-break: break-word;
}

.bio-text {
  line-height: 1.5;
  color: #444;
}

@media (max-width: 500px) {
  .profile-modal {
    padding: 24px;
    border-radius: 20px;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
    font-size: 32px;
    border-radius: 16px;
  }

  .profile-header h2 {
    font-size: 22px;
  }
}

.message-meta-wrapper {
  position: relative;
  display: flex;
  margin-top: 8px;
}

.message-bubble.incoming .message-meta-wrapper {
  justify-content: flex-end;
}

.message-bubble.outgoing .message-meta-wrapper {
  justify-content: flex-start;
}

.message-meta {
  position: relative;
  display: inline-block;
  font-size: 12px;
  opacity: 0.9;
  font-weight: 600;
  cursor: default;
  user-select: none;
}

.message-bubble.incoming .message-meta {
  color: #667eea;
}

.message-bubble.outgoing .message-meta {
  color: rgba(255, 255, 255, 0.95);
}

.custom-tooltip {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%) translateY(4px);
  background: rgba(25, 25, 35, 0.94);
  color: #e0e0ff;
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 8px;
  backdrop-filter: blur(10px);
  box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.25),
      0 0 0 1px rgba(102, 126, 234, 0.3);
  z-index: 10;
  pointer-events: none;
  opacity: 0;
  animation: tooltip-fade-in 0.18s ease-out forwards;
  white-space: nowrap;
  max-width: 200px;
  text-align: center;
}

@keyframes tooltip-fade-in {
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(8px);
  }
}
</style>