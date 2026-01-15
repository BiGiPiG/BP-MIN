<template>
  <div
      ref="messagesContainer"
      class="messages-container"
      :class="{ 'scroll-locked': isScrollLocked, 'scrollbar-visible': isScrollbarVisible }"
      @contextmenu="preventDefaultContextMenu"
      @scroll="onScroll"
      @mouseenter="onScroll"
      @mouseleave="onScroll"
  >
    <div
        v-for="message in messages"
        :key="message.id"
        class="message-container"
    >
      <div
          :class="{
          'message-bubble outgoing': isOutgoing(message),
          'message-bubble incoming': !isOutgoing(message)
        }"
          @contextmenu="(e) => onContextMenu(e, message)"
      >
        <!-- Edit mode -->
        <div v-if="editingMessageId === message.id" class="edit-mode">
          <input
              v-model="editingContent"
              @keyup.enter="saveEdit"
              @keyup.esc="cancelEdit"
              class="edit-input"
              autofocus
          />
          <div class="edit-actions">
            <button @click="saveEdit" class="edit-save">Save</button>
            <button @click="cancelEdit" class="edit-cancel">Cancel</button>
          </div>
        </div>

        <!-- View mode -->
        <template v-else>
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
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'

const props = defineProps({
  messages: { type: Array, required: true },
  currentUserId: { type: Number, required: true },
  isScrollLocked: { type: Boolean, default: false },
  editingMessageId: { type: [String, Number, null], default: null }
})

const emit = defineEmits([
  'contextmenu',
  'scroll',
  'edit-start',
  'edit-save',
  'edit-cancel'
])

// State
const messagesContainer = ref(null)
const hoveredMessageId = ref(null)
const isScrollbarVisible = ref(true)
const editingContent = ref('')
let scrollActivityTimeout = null

// Sync editing content when message changes
watch(() => props.editingMessageId, (id) => {
  if (id) {
    const msg = props.messages.find(m => m.id === id)
    editingContent.value = msg?.content || ''
  } else {
    editingContent.value = ''
  }
})

const isOutgoing = (message) => String(message.senderId) === String(props.currentUserId)

const showTooltip = (id) => hoveredMessageId.value = id
const hideTooltip = () => hoveredMessageId.value = null

const preventDefaultContextMenu = (e) => e.preventDefault()

const onContextMenu = (e, message) => {
  if (isOutgoing(message)) {
    emit('contextmenu', e, message)
  }
}

const onScroll = () => {
  isScrollbarVisible.value = true
  if (scrollActivityTimeout) clearTimeout(scrollActivityTimeout)
  scrollActivityTimeout = setTimeout(() => {
    isScrollbarVisible.value = false
  }, 1500)
  emit('scroll')
}

const saveEdit = () => {
  const content = editingContent.value.trim()
  if (content && props.editingMessageId) {
    emit('edit-save', { id: props.editingMessageId, content })
  }
}

const cancelEdit = () => {
  emit('edit-cancel')
}

defineExpose({
  scrollToBottom: () => {
    nextTick(() => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    })
  }
})
</script>

<style scoped>
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
  animation: bubble-appear 0.4s ease-out;
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

@keyframes tooltip-fade-in {
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(8px);
  }
}

@media (max-width: 768px) {
  .messages-container {
    padding: 20px 16px;
    gap: 16px;
  }

  .message-bubble {
    max-width: 80%;
    padding: 16px 20px;
  }
}

.edit-mode {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.edit-input {
  padding: 10px 12px;
  border: 2px solid #667eea;
  border-radius: 12px;
  font-size: 15px;
  outline: none;
  background: white;
  color: #333;
  min-width: 200px;
  max-width: 100%;
}

.edit-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.edit-save,
.edit-cancel {
  padding: 6px 12px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 600;
}

.edit-save {
  background: #667eea;
  color: white;
}

.edit-cancel {
  background: #f0f0f0;
  color: #555;
}

.edit-save:hover {
  background: #5a6fd8;
}

.edit-cancel:hover {
  background: #e0e0e0;
}
</style>