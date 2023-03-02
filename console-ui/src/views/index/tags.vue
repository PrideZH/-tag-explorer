<script setup lang="ts">
import { onMounted, ref } from 'vue';
import tagApi, { Tag } from '../../api/tag';

const emits = defineEmits([ 'search' ])

const tags = ref<Tag[]>([]);
const actives = ref<string[]>([]);

const activeHandle = (id: string) => {
  let i = 0;
  for (; i < tags.value.length; i++) {
    if (actives.value[i] == id) {
      break;
    }
  }
  if (i == tags.value.length) {
    actives.value.push(id);
  } else {
    actives.value.splice(i, 1);
  }
  listRequest()
  emits('search', actives.value)
}

onMounted(() => listRequest())

function listRequest () {
  tagApi.list(actives.value).then(res => {
    tags.value = res.data;
  })
}
</script>

<template>
  <div id="tags-container">
    <span :class="[ 'tag', { 'tag-active': actives.includes(tag.id) } ]" v-for="tag in tags" :key="tag.id"
        @click="activeHandle(tag.id)">
        {{ tag.name }} [{{ tag.count }}]
    </span>
  </div>
</template>

<style scoped>
#tags-container {
  overflow-y: auto;
}

.tag {
  display: inline-block;
  margin: 8px;
  padding: 2px 4px;
  border: none;
  border-radius: 4px;
  color: #646464;
  background-color: #f3f3f3;
  font-size: 0.8rem;
  cursor: pointer;
}

.tag-active {
  color: #fff;
  background-color: #eb4a76;
}
</style>