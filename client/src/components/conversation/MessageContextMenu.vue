<template>
  <teleport to="body">
    <div
        v-if="visible"
        class="context-menu"
        :style="{ left: x + 'px', top: y + 'px' }"
        @click.stop
    >
      <button @click="onEdit" class="context-menu-item">Edit</button>
      <button @click="onDelete" class="context-menu-item delete">Delete</button>
    </div>
  </teleport>
</template>

<script setup>
const props = defineProps({
  visible: { type: Boolean, required: true },
  x: { type: Number, required: true },
  y: { type: Number, required: true },
  messageId: { type: [Number], required: true }
})

const emit = defineEmits(['edit', 'delete', 'close'])

const onEdit = () => {
  if (props.messageId != null) {
    emit('edit', props.messageId)
  }
  emit('close')
}

const onDelete = () => {
  if (props.messageId != null) {
    emit('delete', props.messageId)
  }
  emit('close')
}
</script>

<style scoped>
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
  transform: translate(-100%, -100%);
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
</style>