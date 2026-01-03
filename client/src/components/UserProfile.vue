<script setup>
import {ref, onMounted, computed} from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const profileData = ref({
  username: '',
  userId: '',
  bio: '',
  birthDate: ''
})

let initialData = {}

onMounted(() => {
  profileData.value = {
    username: localStorage.getItem('username') || 'User',
    userId: localStorage.getItem('userId') || '',
    bio: localStorage.getItem('userBio') || '',
    birthDate: localStorage.getItem('userBirthDate') || ''
  }
  initialData = { ...profileData.value }
})

const profileInitial = computed(() => {
  return profileData.value.username.charAt(0).toUpperCase() || '?'
})

const profileGradient = 'linear-gradient(135deg, #7e4aff 0%, #a78bfa 100%)'

const saveChanges = () => {
  const { username, userId, bio, birthDate } = profileData.value
  localStorage.setItem('username', username)
  localStorage.setItem('userId', userId)
  localStorage.setItem('userBio', bio)
  localStorage.setItem('userBirthDate', birthDate)
  initialData = { ...profileData.value }
  alert('Изменения сохранены!')
}

const discardChanges = () => {
  profileData.value = { ...initialData }
}

const goBack = () => {
  router.go(-1)
}
</script>

<template>
  <div class="user-profile-outer">
    <div class="user-profile-container">
      <!-- Заголовок -->
      <div class="profile-header">
        <button class="back-button" @click="goBack" aria-label="Назад">
          ←
        </button>
        <h1 class="profile-title">Мой профиль</h1>
      </div>

      <!-- Аватар -->
      <div class="avatar-section">
        <div class="avatar-large" :style="{ background: profileGradient }">
          {{ profileInitial }}
        </div>
      </div>

      <!-- Поля профиля -->
      <div class="fields-grid">
        <div class="field">
          <label class="field-label">Имя пользователя</label>
          <input
              v-model="profileData.username"
              type="text"
              class="field-input"
              placeholder="Введите имя"
          />
        </div>

        <div class="field">
          <label class="field-label">ID пользователя</label>
          <input
              v-model="profileData.userId"
              type="text"
              class="field-input"
              placeholder="Введите ID"
          />
        </div>

        <div class="field full-width">
          <label class="field-label">Дата рождения</label>
          <input
              v-model="profileData.birthDate"
              type="date"
              class="field-input"
          />
        </div>

        <div class="field full-width">
          <label class="field-label">О себе</label>
          <textarea
              v-model="profileData.bio"
              class="field-textarea"
              placeholder="Расскажите о себе..."
              rows="5"
          ></textarea>
        </div>
      </div>

      <!-- Кнопки управления -->
      <div class="actions">
        <button class="btn btn-secondary" @click="discardChanges">
          Отменить
        </button>
        <button class="btn btn-primary" @click="saveChanges">
          Сохранить изменения
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Внешний контейнер — полупрозрачный фон с blur (эффект "стекла") */
.user-profile-outer {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100dvh;
  padding: 20px;
  box-sizing: border-box;
  /* Фон убран — он будет снаружи, как в чатах */
}

/* Панель профиля — чисто белая, как в ChatList и SearchBar */
.user-profile-container {
  width: 100%;
  max-width: 760px;
  min-width: 300px;
  padding: 48px 40px;
  background: white; /* как в sideBar */
  border-radius: 24px;
  box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.12),
      0 8px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0; /* как в sideBar */
  font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
  display: flex;
  flex-direction: column;
  gap: 32px;
  box-sizing: border-box;
  overflow-y: auto;
  max-height: 100%;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.user-profile-container::-webkit-scrollbar {
  display: none;
}

/* Остальной CSS без изменений, но убираем backdrop-filter и прозрачность */
.profile-header,
.avatar-section,
.fields-grid,
.actions {
  flex-shrink: 0;
}

.back-button {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: #f0ebff;
  border: none;
  font-size: 20px;
  color: #7e4aff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.back-button:hover {
  background: #e0d6ff;
  transform: translateX(-2px);
}

.profile-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  line-height: 1.2;
  text-align: center;
}

/* Секция с аватаром */
.avatar-section {
  display: flex;
  justify-content: center;
  margin: 8px 0;
  flex-shrink: 0;
}

.avatar-large {
  width: 120px;
  height: 120px;
  border-radius: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 42px;
  box-shadow:
      0 12px 40px rgba(126, 74, 255, 0.35),
      0 6px 20px rgba(249, 115, 22, 0.25);
  transition: transform 0.3s ease;
  flex-shrink: 0;
}

.avatar-large:hover {
  transform: scale(1.05);
}

/* Сетка полей */
.fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
  margin: 8px 0;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field.full-width {
  grid-column: span 2;
}

.field-label {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  padding-left: 4px;
}

.field-input,
.field-textarea {
  width: 100%;
  padding: 16px 20px;
  border: 2px solid #e8e8e8;
  border-radius: 16px;
  font-size: 15px;
  font-family: inherit;
  outline: none;
  background: #f8f9fa;
  transition: all 0.25s ease;
  box-sizing: border-box;
}

.field-input:focus,
.field-textarea:focus {
  border-color: #7e4aff;
  background: white;
  box-shadow: 0 0 0 4px rgba(126, 74, 255, 0.15);
}

.field-textarea {
  min-height: 120px;
  max-height: 120px;
  resize: none;
  overflow-y: auto;
  line-height: 1.5;
}

.field-textarea::-webkit-scrollbar {
  width: 6px;
}

.field-textarea::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.field-textarea::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.field-textarea::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* Кнопки */
.actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  flex-shrink: 0; /* кнопки не должны сжиматься */
}

.btn {
  flex: 1;
  padding: 16px 24px;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  min-height: 52px;
}

.btn-primary {
  background: linear-gradient(135deg, #7e4aff, #a78bfa);
  color: white;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #6a2c91, #8b5cf6);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(126, 74, 255, 0.3);
}

.btn-secondary {
  background: #f5f5f5;
  color: #444;
}

.btn-secondary:hover {
  background: #e8e8e8;
  transform: translateY(-2px);
}

/* Адаптивность */
@media (max-width: 768px) {
  .user-profile-outer {
    padding: 16px;
  }

  .user-profile-container {
    padding: 36px 28px;
    gap: 28px;
    border-radius: 20px;
  }

  .fields-grid {
    grid-template-columns: 1fr;
    gap: 22px;
  }

  .field.full-width {
    grid-column: span 1;
  }

  .profile-title {
    font-size: 24px;
  }

  .avatar-large {
    width: 100px;
    height: 100px;
    font-size: 36px;
    border-radius: 24px;
  }

  .field-textarea {
    min-height: 100px;
    max-height: 100px;
  }

  .actions {
    flex-direction: column;
    gap: 12px;
  }

  .btn {
    min-height: 48px;
  }
}

@media (max-width: 480px) {
  .user-profile-container {
    padding: 28px 22px;
    gap: 24px;
    border-radius: 18px;
  }

  .profile-header {
    gap: 12px;
  }

  .back-button {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }

  .profile-title {
    font-size: 22px;
  }

  .avatar-large {
    width: 90px;
    height: 90px;
    font-size: 32px;
  }

  .field-input,
  .field-textarea {
    padding: 14px 18px;
  }

  .field-textarea {
    min-height: 90px;
    max-height: 90px;
  }
}
</style>