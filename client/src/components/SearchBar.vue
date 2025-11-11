<script setup>
import { ref, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'

const props = defineProps({
  placeholder: {
    type: String,
    default: 'Search users...'
  },
  apiUrl: {
    type: String,
    default: '/api/users/search'
  }
})

const emit = defineEmits(['user-selected', 'search-change'])

const searchQuery = ref('')
const isFocused = ref(false)
const isLoading = ref(false)
const searchResults = ref([])
const error = ref(null)

const performSearch = useDebounceFn(async (query) => {
  if (!query.trim()) {
    searchResults.value = []
    return
  }

  isLoading.value = true
  error.value = null

  try {
    const params = new URLSearchParams();
    params.append('searchTerm', query);

    const response = await fetch(`/api/users/search?${params.toString()}`, {
      method: "GET",
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const users = await response.json()
    searchResults.value = users
  } catch (err) {
    console.error('Search error:', err)
    error.value = 'Failed to load users'
    searchResults.value = []
  } finally {
    isLoading.value = false
  }
}, 300)

watch(searchQuery, (newQuery) => {
  emit('search-change', newQuery)
  performSearch(newQuery)
})

const handleInput = (event) => {
  searchQuery.value = event.target.value
}

const selectUser = (user) => {
  searchQuery.value = ''
  searchResults.value = []
  emit('user-selected', user.username)
}

const clearSearch = () => {
  searchQuery.value = ''
  searchResults.value = []
  emit('search-change', '')
}
</script>

<template>
  <div class="search-bar-container">
    <div class="search-input-wrapper" :class="{ focused: isFocused }">
      <span class="search-icon">🔍</span>
      <input
          v-model="searchQuery"
          type="text"
          :placeholder="placeholder"
          class="search-input"
          @focus="isFocused = true"
          @blur="isFocused = false"
          @input="handleInput"
      />
      <div v-if="isLoading" class="loading-spinner"></div>
      <button
          v-if="searchQuery"
          @click="clearSearch"
          class="clear-btn"
      >
        ✕
      </button>
    </div>

    <div v-if="searchResults.length > 0 && searchQuery" class="search-results">
      <div
          v-for="user in searchResults"
          :key="user.id"
          class="user-result"
          @mousedown="selectUser(user)"
      >
        <div class="user-avatar">
          {{ (user.username)?.charAt(0)?.toUpperCase() }}
        </div>
        <div class="user-info">
          <div class="user-name">{{ user.username }}</div>
        </div>
      </div>
    </div>

    <div v-else-if="searchQuery && searchResults.length === 0 && !isLoading" class="no-results">
      <div class="no-results-icon">😕</div>
      <div class="no-results-text">No users found</div>
    </div>
  </div>
</template>

<style scoped>
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #7e4aff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-left: 8px;
  flex-shrink: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.search-bar-container {
  position: relative;
  width: 98%;
  padding: 1%;
  height: 50px;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #f8f7ff, #fff7ed);
  border: 2px solid rgba(126, 74, 255, 0.2);
  border-radius: 16px;
  padding: 8px 16px;
  transition: all 0.3s ease;
  position: relative;
}

.search-input-wrapper.focused {
  border-color: #7e4aff;
  background: white;
  box-shadow:
      0 0 0 4px rgba(126, 74, 255, 0.15),
      0 4px 20px rgba(126, 74, 255, 0.1);
}

.search-icon {
  color: #a78bfa;
  font-size: 16px;
  margin-right: 12px;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.search-input::placeholder {
  color: #a78bfa;
  font-weight: 400;
}

.clear-btn {
  background: linear-gradient(135deg, #f97316, #fdba74);
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
  margin-left: 8px;
}

.clear-btn:hover {
  transform: scale(1.1);
  background: linear-gradient(135deg, #ea580c, #f97316);
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 16px;
  margin-top: 8px;
  box-shadow:
      0 10px 40px rgba(0, 0, 0, 0.15),
      0 4px 20px rgba(126, 74, 255, 0.1);
  border: 1px solid rgba(126, 74, 255, 0.1);
  max-height: 300px;
  overflow-y: auto;
  z-index: 1000;
  animation: slideDown 0.2s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-result {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 1px solid rgba(126, 74, 255, 0.05);
}

.user-result:last-child {
  border-bottom: none;
}

.user-result:hover {
  background: linear-gradient(135deg, #faf8ff, #fff7ed);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #7e4aff, #f97316);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 16px;
  margin-right: 12px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(126, 74, 255, 0.3);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-nick {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-name {
  font-size: 13px;
  color: #8b5cf6;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.no-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 16px;
  margin-top: 8px;
  padding: 24px 16px;
  text-align: center;
  box-shadow:
      0 10px 40px rgba(0, 0, 0, 0.15),
      0 4px 20px rgba(126, 74, 255, 0.1);
  border: 1px solid rgba(126, 74, 255, 0.1);
  z-index: 1000;
  animation: slideDown 0.2s ease-out;
}

.no-results-icon {
  font-size: 32px;
  margin-bottom: 8px;
  opacity: 0.7;
}

.no-results-text {
  font-size: 14px;
  color: #8b5cf6;
  font-weight: 500;
}

.search-results::-webkit-scrollbar {
  width: 6px;
}

.search-results::-webkit-scrollbar-track {
  background: rgba(126, 74, 255, 0.05);
  border-radius: 3px;
}

.search-results::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #7e4aff, #f97316);
  border-radius: 3px;
}

.search-results::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #f97316, #7e4aff);
}

@media (max-width: 768px) {
  .search-input-wrapper {
    padding: 6px 14px;
  }

  .search-input {
    font-size: 14px;
  }

  .user-result {
    padding: 10px 14px;
  }

  .user-avatar {
    width: 36px;
    height: 36px;
    font-size: 14px;
  }

  .user-nick {
    font-size: 14px;
  }

  .user-name {
    font-size: 12px;
  }
}
</style>