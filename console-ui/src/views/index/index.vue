<script setup lang="ts">
import { onMounted, ref } from 'vue'

import Resource from './resource.vue'
import Tags from './tags.vue'
import Header from './header.vue'
import Console from './console.vue'

import resourceApi, { ResourceItem } from '../../api/resource'
import { Page } from '../../types'

const resources = ref<Page<ResourceItem>>()

const active = ref<ResourceItem>();
const tags = ref<string[]>([]);
const current = ref<number>(1);

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
</script>

<template>
  <div class="index-container">
    <Tags class="tags" @search="data => {
      tags = data;
      requestResource();
    }"></Tags>
    <div class="main">
      <Header class="header" @insert="(data: ResourceItem[]) => {
        if (resources == undefined) return;
        data.forEach(item => resources?.records.unshift(item));
        resources.total++;
      }"></Header>
      <div class="resources-scroll">
        <div class="resources">
          <Resource :class="[ 'resource', { 'resource-active': resource == active } ]"
            v-for="resource in resources?.records" :key="resource.id" :resource="resource"
            @click="() => active = resource">
          </Resource>
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
        <span :class="['item', {'item-active': resources?.pages == current}]" @click="changePage(resources?.pages || 1)">{{ resources?.pages }}</span>
        <span class="item">
          <i class='bx bx-chevron-right' ></i>
        </span>
      </div>
    </div>
    <Console class="console" :id="active?.id" @delete="() => {
      if (resources == undefined || active == undefined) return;
      resources.records.splice(resources.records.indexOf(active), 1);
      resources.total--;
    }" @update="(name: string) => {
      if (active == undefined) return;
      active.name = name;
    }"></Console>
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
</style>
