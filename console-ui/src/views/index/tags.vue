<script setup lang="ts">
import { onMounted, ref } from 'vue';
import tagApi, { Tag } from '../../api/tag';

const emits = defineEmits([ 'search' ])

const tags = ref<Tag[]>([]);
const actives = ref<Tag[]>([]);

const activeHandle = (tag: Tag) => {
    if (actives.value.includes(tag)) {
        actives.value.splice(actives.value.indexOf(tag), 1);
    } else {
        actives.value.push(tag);
    }
    emits('search', actives.value.map(tag => tag.id))
}

onMounted(() => {
  tagApi.list().then(res => {
    tags.value = res.data;
  })
})
</script>

<template>
  <div id="container">
    <span :class="[ 'tag', { 'tag-active': actives.includes(tag) } ]" v-for="tag in tags" :key="tag.id"
        @click="activeHandle(tag)">
        {{ tag.name }} [{{ tag.count }}]
    </span>
  </div>
</template>

<style scoped>
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