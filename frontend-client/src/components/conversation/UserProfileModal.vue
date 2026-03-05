<template>
  <teleport to="body">
    <div class="profile-overlay" @click="onClose">
      <div class="profile-modal" @click.stop>
        <div class="profile-header">
          <button class="close-button" @click="onClose">×</button>
          <h2>User Profile</h2>
        </div>

        <div class="profile-avatar-section">
          <div
              class="profile-avatar"
              :style="{ background: data.profileColor }"
          >
            {{ initial }}
          </div>
        </div>

        <div class="profile-info">
          <div class="profile-field">
            <label>Username</label>
            <div>{{ data.nickname }}</div>
          </div>
          <div class="profile-field">
            <label>User ID</label>
            <div>{{ data.username }}</div>
          </div>
          <div class="profile-field">
            <label>Birth Date</label>
            <div>{{ data.birthDate || '—' }}</div>
          </div>
          <div class="profile-field">
            <label>About</label>
            <div class="bio-text">{{ data.bio }}</div>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup>
defineProps({
  data: { type: Object, required: true },
  initial: { type: String, required: true }
})

const emit = defineEmits(['close'])

const onClose = () => emit('close')
</script>

<style scoped>
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
</style>