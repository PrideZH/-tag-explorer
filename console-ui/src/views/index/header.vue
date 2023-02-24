<script setup lang="ts">
import axios, { AxiosProgressEvent } from 'axios';
import { ref } from 'vue';
import { ResourceItem } from '../../api/resource';

const emits = defineEmits([ 'insert' ])

const fileRef = ref();

enum WorkStatus {
    WAITING, SUCCESS
}
interface Work {
    name: string;
    progress: number;
    status: WorkStatus;
}
const workQueue = ref<Work[]>([])

const importHandle = (e: Event) => {
    if (e.target === null) return;
    const input: HTMLInputElement = <HTMLInputElement>e.target;
    if (input.files === null) return;
    const files: File[] = Array.from(input.files);

    const formdata = new FormData();
    files.forEach((file: File) => {
        workQueue.value.push({
            name: file.name,
            status: WorkStatus.WAITING,
            progress: 0
        })
        formdata.append('files', file);
    })
    axios.post<ResourceItem>('/resource', formdata, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: function (event: AxiosProgressEvent) {
        workQueue.value.forEach(work => {
            work.progress = event.progress || 0;
        })
      }
    }).then(res => {
        workQueue.value.splice(0, workQueue.value.length);
        emits('insert', res.data);
    })
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

    <div class="work-list" v-show="workQueue.length != 0">
        <div v-for="work in workQueue" class="work-item">
            {{ work.name }}
            <div class="progress" :style="{width: (work.progress * 100) + '%'}"></div>
        </div>
    </div>
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

.work-list {
    position: relative;
}

.work-item {
    position: relative;
}

.work-item .progress {
    position: absolute;
    top: 0;
    height: 100%;
    background-color: #2da44e66;
}
</style>