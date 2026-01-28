<template>
  <div
      ref="messagesContainer"
      class="messages-container"
      :class="{
      'scroll-locked': isScrollbarLocked,
      'scrollbar-visible': isScrollbarVisible
    }"
      @contextmenu="preventDefaultContextMenu"
      @scroll="handleScrollActivity"
      @mouseenter="handleScrollActivity"
      @mouseleave="handleScrollActivity"
  >
    <div
        v-for="message in messages"
        :key="message.id"
        class="message-container"
        :data-message-id="message.id"
    >
      <div
          :class="[
          'message-bubble',
          isOutgoing(message) ? 'outgoing' : 'incoming'
        ]"
          @contextmenu="(e) => handleContextMenu(e, message)"
      >
        <!-- Режим редактирования -->
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

        <!-- Режим просмотра -->
        <template v-else>
          <div class="message-content">{{ message.content }}</div>

          <div class="message-meta">
            <time
                class="message-time"
                :class="{ 'outgoing-time': isOutgoing(message) }"
                @mouseenter="showTooltip(message.id)"
                @mouseleave="hideTooltip"
            >
              {{ message.shortSentAt }}
              <span v-if="hoveredMessageId === message.id" class="tooltip">
                {{ message.fullSentAt }}
              </span>
            </time>

            <div v-if="isOutgoing(message)" class="status-indicator">
              <StatusIcon
                  :status="getMessageStatus(message)"
                  :is-outgoing="true"
              />
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch, onMounted, onUnmounted } from 'vue'
import StatusIcon from './StatusIcon.vue' // Вынесен в отдельный компонент

// =============== ПРОПСЫ И ЭМИТЫ ===============
const props = defineProps({
  messages: { type: Array, required: true },
  currentUserId: { type: Number, required: true },
  isScrollbarLocked: { type: Boolean, default: false }, // Исправлено: используется напрямую в шаблоне
  editingMessageId: { type: [String, Number, null], default: null }
})

const emit = defineEmits([
  'contextmenu', 'scroll', 'edit-save', 'edit-cancel', 'message-read'
])

// =============== СОСТОЯНИЕ ===============
const messagesContainer = ref(null)
const hoveredMessageId = ref(null)
const isScrollbarVisible = ref(true)
const editingContent = ref('')
const messageElements = ref(new Map())
const observer = ref(null)
const readTimeouts = ref({})
const readMessages = ref(new Set())
let scrollTimeout = null

// =============== ВЫЧИСЛЯЕМЫЕ СВОЙСТВА ===============
const isOutgoing = (message) =>
    String(message.senderId) === String(props.currentUserId)

const getMessageStatus = (message) => {
  if (message.isRead) return 'read'
  if (message.isSend) return 'delivered'
  return 'sending'
}

// =============== НАБЛЮДАТЕЛИ ===============
watch(() => props.editingMessageId, (id) => {
  if (id) {
    const msg = props.messages.find(m => m.id === id)
    editingContent.value = msg?.content || ''
  } else {
    editingContent.value = ''
  }
})

watch(() => props.messages, async () => {
  await nextTick()
  updateObservers()
}, { deep: true })

// =============== ЖИЗНЕННЫЙ ЦИКЛ ===============
onMounted(() => {
  initIntersectionObserver()
  updateObservers()
})

onUnmounted(() => {
  observer.value?.disconnect()
  Object.values(readTimeouts.value).forEach(clearTimeout)
})

// =============== МЕТОДЫ НАБЛЮДАТЕЛЯ ===============
const initIntersectionObserver = () => {
  observer.value = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      const messageId = entry.target.dataset.messageId
      if (!messageId) return

      const message = props.messages.find(m => String(m.id) === messageId)
      if (!message || isOutgoing(message)) return

      if (entry.isIntersecting && entry.intersectionRatio >= 0.85) {
        scheduleReadConfirmation(messageId, message.chatId)
      } else {
        cancelReadConfirmation(messageId)
      }
    })
  }, {
    threshold: 0.85,
    root: messagesContainer.value
  })
}

const updateObservers = () => {
  messageElements.value.forEach(el => observer.value?.unobserve(el))
  messageElements.value.clear()

  props.messages.forEach(message => {
    const el = messagesContainer.value?.querySelector(`[data-message-id="${message.id}"]`)
    if (el) {
      messageElements.value.set(String(message.id), el)
      observer.value?.observe(el)
    }
  })
}

// =============== ПОДТВЕРЖДЕНИЕ ПРОЧТЕНИЯ ===============
const scheduleReadConfirmation = (messageId, chatId) => {
  if (readMessages.value.has(messageId)) return
  cancelReadConfirmation(messageId)

  readTimeouts.value[messageId] = setTimeout(() => {
    readMessages.value.add(messageId)
    emit('message-read', { messageId, chatId })
    delete readTimeouts.value[messageId]
  }, 1200)
}

const cancelReadConfirmation = (messageId) => {
  if (readTimeouts.value[messageId]) {
    clearTimeout(readTimeouts.value[messageId])
    delete readTimeouts.value[messageId]
  }
}

// =============== ОБРАБОТЧИКИ СОБЫТИЙ ===============
const handleScrollActivity = () => {
  isScrollbarVisible.value = true
  clearTimeout(scrollTimeout)
  scrollTimeout = setTimeout(() => {
    isScrollbarVisible.value = false
  }, 1500)
  emit('scroll')
}

const handleContextMenu = (e, message) => {
  if (isOutgoing(message)) emit('contextmenu', e, message)
}

const preventDefaultContextMenu = (e) => e.preventDefault()

const showTooltip = (id) => hoveredMessageId.value = id
const hideTooltip = () => hoveredMessageId.value = null

// =============== РЕДАКТИРОВАНИЕ СООБЩЕНИЙ ===============
const saveEdit = () => {
  const content = editingContent.value.trim()
  if (content && props.editingMessageId) {
    emit('edit-save', { id: props.editingMessageId, content })
  }
}

const cancelEdit = () => emit('edit-cancel')

// =============== ЭКСПОРТ МЕТОДОВ ===============
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
/* Контейнер сообщений */
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
  transition: overflow 0.3s ease;
}

.messages-container.scroll-locked {
  overflow: hidden;
}

/* Стили скроллбара */
.messages-container::-webkit-scrollbar { width: 8px; }
.messages-container::-webkit-scrollbar-track { background: transparent; border-radius: 4px; }
.messages-container::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea, #f093fb);
  border-radius: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}
.messages-container.scrollbar-visible::-webkit-scrollbar-thumb { opacity: 1; }
.messages-container:not(.scrollbar-visible) { scrollbar-color: transparent transparent; }
.messages-container.scrollbar-visible { scrollbar-color: #667eea rgba(102, 126, 234, 0.08); }

/* Сообщения */
.message-container {
  display: flex;
  width: 100%;
}
.message-container .outgoing { margin-left: auto; }
.message-container .incoming { margin-right: auto; }

.message-bubble {
  max-width: 65%;
  min-width: 50px;
  padding: 18px 22px;
  border-radius: 22px;
  position: relative;
  word-break: break-word;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(8px);
  animation: bubble-appear 0.4s ease-out;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.message-bubble:hover { transform: translateY(-3px); box-shadow: 0 8px 25px rgba(0, 0, 0, 0.18); }

/* Стили входящих/исходящих */
.message-bubble.incoming {
  background: linear-gradient(135deg, #ffffff, #f8faff);
  border: 1px solid rgba(102, 126, 234, 0.15);
  border-radius: 8px 22px 22px 8px;
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
  border-radius: 22px 8px 8px 22px;
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

/* Метаданные сообщения */
.message-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  justify-content: flex-end;
}
.message-bubble.outgoing .message-meta { justify-content: flex-start; }

.message-time {
  position: relative;
  font-size: 12px;
  font-weight: 600;
  opacity: 0.9;
  cursor: default;
  white-space: nowrap;
}
.message-bubble.incoming .message-time { color: #667eea; }
.message-bubble.outgoing .message-time { color: rgba(255, 255, 255, 0.95); }

/* Tooltip времени */
.tooltip {
  position: absolute;
  bottom: -24px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(25, 25, 35, 0.94);
  color: #e0e0ff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 6px;
  white-space: nowrap;
  animation: tooltip-fade-in 0.18s ease-out forwards;
  pointer-events: none;
}

/* Режим редактирования */
.edit-mode { display: flex; flex-direction: column; gap: 8px; width: 100%; }
.edit-input {
  padding: 10px 12px;
  border: 2px solid #667eea;
  border-radius: 12px;
  font-size: 15px;
  outline: none;
  background: white;
  color: #333;
}
.edit-actions { display: flex; justify-content: flex-end; gap: 8px; }
.edit-save, .edit-cancel {
  padding: 6px 12px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}
.edit-save { background: #667eea; color: white; }
.edit-cancel { background: #f0f0f0; color: #555; }
.edit-save:hover { background: #5a6fd8; }
.edit-cancel:hover { background: #e0e0e0; }

/* Анимации */
@keyframes bubble-appear {
  from { opacity: 0; transform: translateY(15px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
@keyframes tooltip-fade-in {
  to { opacity: 1; transform: translateX(-50%) translateY(4px); }
}

/* Адаптивность */
@media (max-width: 768px) {
  .messages-container { padding: 20px 16px; gap: 16px; }
  .message-bubble { max-width: 80%; padding: 16px 20px; }
  .message-time { font-size: 11px; }
  .status-indicator { width: 16px; height: 16px; }
}
</style>