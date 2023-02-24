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

onMounted(() => {
  requestResource();
})

function requestResource (tags?: string[]) {
  resourceApi.list(tags).then(res => {
    resources.value = res.data
  });
}
</script>

<template>
  <div class="index-container">
    <Tags class="tags" @search="tags => {
      requestResource(tags);
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
</style>
