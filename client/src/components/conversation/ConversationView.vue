<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useMessageStore } from '@/utils/useMessages'

// Child components
import ConversationPlaceholder from './ConversationPlaceholder.vue'
import ConversationHeader from './ConversationHeader.vue'
import MessageList from './MessageList.vue'
import MessageComposer from './MessageComposer.vue'
import UserProfileModal from './UserProfileModal.vue'
import MessageContextMenu from './MessageContextMenu.vue'

// Props & Emits
const props = defineProps({
  currentConversationName: String,
  currentConversation: [Object, null],
  currentInterlocutor: String
})

const emit = defineEmits(['return-to-list', 'send-message', 'delete-message', 'edit-message', `message-read`])

// Reactive state
const isProfileVisible = ref(false)
const messageText = ref('')
const currentUserId = Number(localStorage.getItem('userId'))

const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  messageId: null
})

const editingMessageId = ref(null)

// Composable
const messageStore = useMessageStore()

// Computed
const loadInterlocutorProfile = async () => {
  if (!props.currentInterlocutor) {
    interlocutorProfile.value = null
    return
  }

  try {
    const res = await fetch(`/api/profiles/${props.currentInterlocutor}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'application/json'
      }
    })

    if (!res.ok) {
      throw new Error('Failed to load profile')
    }

    interlocutorProfile.value = await res.json()
  } catch (err) {
    console.error('Error loading interlocutor profile:', err)
    interlocutorProfile.value = null
  }
}

const profileInitial = computed(() => {
  return interlocutorProfile.value.username.charAt(0).toUpperCase() || '?'
})

const conversationHistory = computed(() => {
  return props.currentConversation?.id
      ? messageStore.getMessages(props.currentConversation.id)
      : []
})

// Refs
const messageListRef = ref(null)
const interlocutorProfile = ref(null)

// При монтировании
onMounted(() => {
  document.addEventListener('click', hideContextMenu)
  loadInterlocutorProfile() // ← загружаем сразу
})

// Watchers
watch(() => props.currentConversation?.id, (chatId) => {
  if (chatId) {
    messageStore.loadMessages(chatId)
  }
})

// При изменении currentInterlocutor
watch(() => props.currentInterlocutor, () => {
  loadInterlocutorProfile()
})

watch(conversationHistory, () => {
  nextTick(() => {
    messageListRef.value?.scrollToBottom()
  })
}, { deep: true })

// Methods — Profile
const openProfile = () => isProfileVisible.value = true
const closeProfile = () => isProfileVisible.value = false

// Methods — Message input
const handleSendMessage = (text) => {
  if (text && props.currentConversationName) {
    emit('send-message', text)
  }
}

// Methods — Context menu
const showContextMenu = (e, message) => {
  e.preventDefault()
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
  contextMenu.value.messageId = null
}

const handleDeleteMessage = () => {
  if (contextMenu.value.messageId) {
    emit('delete-message', contextMenu.value.messageId)
  }
  hideContextMenu()
}

const handleMessageRead = ({ messageId, chatId }) => {
  emit('message-read', { messageId, chatId })
}

const startEditing = (messageId) => {
  editingMessageId.value = messageId
  hideContextMenu()
}

const handleEditSave = ({ id, content }) => {
  emit('edit-message', { id, content })
  editingMessageId.value = null
}

const handleEditCancel = () => {
  editingMessageId.value = null
}

// Lifecycle
onMounted(() => {
  document.addEventListener('click', hideContextMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', hideContextMenu)
})
</script>

<template>
  <ConversationPlaceholder v-if="!currentConversationName" />

  <div v-else class="conversation-interface">
    <ConversationHeader
        :contact-name="currentConversationName"
        @back="$emit('return-to-list')"
        @open-profile="openProfile"
    />

    <MessageList
        ref="messageListRef"
        :messages="conversationHistory"
        :current-user-id="currentUserId"
        :is-scroll-locked="contextMenu.visible"
        :editing-message-id="editingMessageId"
        @contextmenu="showContextMenu"
        @edit-save="handleEditSave"
        @edit-cancel="handleEditCancel"
        @message-read="handleMessageRead"
    />

    <MessageComposer
        v-model="messageText"
        @send="handleSendMessage"
    />

    <UserProfileModal
        v-if="isProfileVisible"
        :data="interlocutorProfile"
        :initial="profileInitial"
        @close="closeProfile"
    />

    <MessageContextMenu
        :visible="contextMenu.visible"
        :x="contextMenu.x"
        :y="contextMenu.y"
        :message-id="contextMenu.messageId"
        @edit="startEditing"
        @delete="handleDeleteMessage"
        @close="hideContextMenu"
    />
  </div>
</template>

<style scoped>
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

@media (max-width: 768px) {
  .conversation-interface {
    border-radius: 0;
  }
}
</style>