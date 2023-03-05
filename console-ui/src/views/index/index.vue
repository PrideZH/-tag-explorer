<script setup lang="ts">
import { onMounted, ref } from 'vue'

import Resource from './resource.vue'
import Tags from './tags.vue'
import Header from './header.vue'
import Console from './console.vue'

import resourceApi, { ResourceItem } from '../../api/resource'
import { Page } from '../../types'
import axios, { AxiosProgressEvent } from 'axios'

const resources = ref<Page<ResourceItem>>()
const deleteResourceHandle = (id: string) => {
  if (resources.value == undefined) return;
  let index = 0;
  for (; index < resources.value.records.length; index++) {
    if (resources.value.records[index].id == id) break;
  }
  if (index == resources.value.records.length) return;
  resources.value.records.splice(index, 1);
  resources.value.total--;

  // 控制台显示的资源被删除时 显示下一个资源
  if (active.value?.id == id) {
    active.value = resources.value.records[Math.min(index, resources.value.records.length - 1)];
  }
}
const updateResourceHandle = (id: string, name: string) => {
  if (resources.value == undefined) return;
  let index = 0;
  for (; index < resources.value.records.length; index++) {
    if (resources.value.records[index].id == id) break;
  }
  if (index == resources.value.records.length) return;
  resources.value.records[index].name = name;
}

const delCoverHandle = (id: string) => {
  if (resources.value == undefined) return;
  let index = 0;
  for (; index < resources.value.records.length; index++) {
    if (resources.value.records[index].id == id) break;
  }
  if (index == resources.value.records.length) return;
  resources.value.records[index].cover = false;
}

const insertCoverHandle = (id: string) => {
  if (resources.value == undefined) return;
  let index = 0;
  for (; index < resources.value.records.length; index++) {
    if (resources.value.records[index].id == id) break;
  }
  if (index == resources.value.records.length) return;
  resources.value.records[index].cover = true;
}

// 控制台显示资源
const active = ref<ResourceItem>()

const tags = ref<string[]>([]);
const current = ref<number>(1);

// 拖拽文件上传
const dragActive = ref<boolean>(false);

enum WorkStatus {
  WAITING, SUCCESS
}
interface Work {
  id: string;
  name: string;
  progress: number;
  status: WorkStatus;
}
const workQueue = ref<Work[]>([])

const dropFilesHandle = (event: DragEvent) => {
  dragActive.value = false;
  if (event.dataTransfer == null) {
    return console.error('数据丢失')
  }
  if (event.dataTransfer.files.length < 1) return
  uploadFiles(Array.from(event.dataTransfer.files))
}

onMounted(() => {
  requestResource();
})

function requestResource () {
  resourceApi.list(tags.value, current.value).then(res => {
    resources.value = res.data
  });
}

function generatePageNum (): number[] {
  let res: number[] = [];
  if (resources.value == undefined) return res;
  let start = current.value - 2, end = current.value + 2;
  for (let i: number = start; i <= end; i++) {
    if (i <= 1) {
      if (end < resources.value.pages) end++;
    } else if (i >= resources.value.pages) {
      if (start > 2) res.unshift(--start);
    } else {
      res.push(i);
    }
  }
  return res;
}

function changePage (page: number) {
  if (page == current.value) return;
  current.value = page;
  requestResource();
}

function uploadFiles (files: File[]) {
  workQueue.value = workQueue.value.filter((work: Work) => work.status == WorkStatus.SUCCESS)

  files.forEach((file: File) => {
    if (file.name.length >= 128) {
      console.error('文件名过长');
      return;
    }

    const id: string = crypto.randomUUID();
    workQueue.value.push({
      id,
      name: file.name,
      status: WorkStatus.WAITING,
      progress: 0
    })

    const formdata = new FormData();
    formdata.append('files', file);

    axios.post<ResourceItem[]>('/resource', formdata, {
      timeout: 60 * 60 * 1000,
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: function (event: AxiosProgressEvent) {
        workQueue.value.forEach(work => {
          work.progress = event.progress || 0;
        })
      }
    }).then(res => {
      if (resources.value == undefined) return;
      res.data.forEach(item => resources.value?.records.unshift(item));
      resources.value.total += res.data.length;
    }).catch(err => {
      console.error(err)
    }).finally(() => {
      for (let i = 0; i < workQueue.value.length; i++) {
        if (workQueue.value[i].id == id) {
          workQueue.value.splice(i, 1)
          return
        }
      }
    })
  })
}
</script>

<template>
  <div class="index-container">
    <Tags class="tags" @search="data => {
      tags = data;
      current = 1;
      requestResource();
    }"></Tags>
    <div class="main">
      <Header class="header" @upload="uploadFiles"></Header>
      <div class="resources-scroll">
        <div :class="['resources', { 'drag-active': dragActive }]"
            @dragenter.stop.prevent="dragActive = true"
            @dragover.stop.prevent="dragActive = true"
            @dragleave.stop.prevent="dragActive = false"
            @drop.stop.prevent="dropFilesHandle">
          <Resource :class="[ 'resource', { 'resource-active': resource == active } ]"
            v-for="resource in resources?.records" :key="resource.id" :resource="resource"
            @click="() => active = resource">
          </Resource>
        </div>
      </div>
      <div class="work-list" v-show="workQueue.length != 0">
        <div v-for="work in workQueue" class="work-item">
            {{ work.name }}
            <div class="progress" :style="{width: (work.progress * 100) + '%'}"></div>
        </div>
      </div>
      <div class="pagination" v-show="resources?.pages || 1 > 1">
        Total: {{ resources?.total }}
        <span class="item">
          <i class='bx bx-chevron-left'></i>
        </span>
        <span :class="['item', {'item-active': 1 == current}]" @click="changePage(1)">1</span>
        <span class="item item-disable" v-show="current > 4">
          <i class='bx bx-dots-horizontal-rounded' ></i>
        </span>
        <span :class="['item', {'item-active': page == current}]" v-for="page in generatePageNum()" @click="changePage(page)">
          {{ page }}
        </span>
        <span class="item item-disable" v-show="current < (resources?.pages || 1) - 3">
          <i class='bx bx-dots-horizontal-rounded' ></i>
        </span>
        <span v-show="resources?.pages != 1" :class="['item', {'item-active': resources?.pages == current}]" @click="changePage(resources?.pages || 1)">{{ resources?.pages }}</span>
        <span class="item">
          <i class='bx bx-chevron-right' ></i>
        </span>
      </div>
    </div>
    <Console class="console" :id="active?.id" @delete="deleteResourceHandle" @update="updateResourceHandle" @delCover="delCoverHandle" @insertCover="insertCoverHandle"></Console>
  </div>
</template>

<style scoped>
.index-container {
  display: flex;

  height: 100%;
}

.main {
  flex: 1;

  display: flex;
  flex-direction: column;
}

.header {
  height: 48px;
}

.resources-scroll {
  flex: 1;
  overflow-y: auto;
}

.resources {
  display: grid;
  grid-gap: 16px;
  grid-template-columns: repeat(5, minmax(0px, 1fr));

  padding: 32px;
}

.drag-active {
  border: 1px dashed #eb4a76;
}

.resource {
  border: 4px solid #f5f5f5;
  border-radius: 8px;
  padding: 4px;
}

.resource-active {
  border: 4px solid #0459c8;
}

.tags {
  width: 200px;
}

.console {
  width: 256px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  padding: 16px;
}

.pagination .item {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 28px;
  height: 28px;
  margin: 0 4px;
  border-radius: 4px;

  cursor: pointer;
}

.pagination .item:hover, .item-active {
  color: #fff;
  background-color: #eb4a76;
}

.pagination .item-disable, .pagination .item-disable:hover {
  color: #dbdef0;
  background-color: transparent;
  cursor: default;
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
