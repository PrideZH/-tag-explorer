<script setup lang="ts">
import { ref } from 'vue';

const emits = defineEmits([ 'upload' ])

const fileRef = ref();

const importHandle = (e: Event) => {
    if (e.target === null) return;
    const input: HTMLInputElement = <HTMLInputElement>e.target;
    if (input.files === null) return;
    const files: File[] = Array.from(input.files);
    emits('upload', files)
}
</script>

<template>
  <div id="header-container">
    <input type="text" />
    <select>
        <option>All</option>
        <option>Image</option>
        <option>Video</option>
        <option>Text</option>
        <option>Unknown</option>
    </select>
    <select>
        <option>Name</option>
        <option>Size</option>
        <option>Import date</option>
    </select>
    <select>
        <option>升序</option>
        <option>降序</option>
    </select>
    <select>
        <option>Grid</option>
        <option>List</option>
    </select>
    <input type="file" multiple ref="fileRef" @change="importHandle" style="display: none;"/>
    <!-- <button class="button">Import</button> -->
    <button class="button" @click="() => fileRef.click()">Upload</button>
  </div>
</template>

<style scoped>
#header-container {
    display: flex;
    align-items: center;
    justify-content: center;
}

.button {
    margin: 0 4px;
    background-color: transparent;
    border: 1px solid #dbdef0;
    border-radius: 6px;
    color: #0d0d0d;
    cursor: pointer;
    font-size: 12px;
    padding: 4px;
    text-align: center;
    text-decoration: none;
}
</style>