<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute();
const id: string = route.query.id as string;
</script>

<template>
  <div class="video-container">
    <video class="video" :src="`/resource/videos/${id}`" controls autoplay type="video/mp4" preload="load"></video>
  </div>
</template>

<style scoped>
.video-container {
  width: 100%;
  height: 100%;
}

.video {
  width: 100%;
  height: 100%;
}
</style>