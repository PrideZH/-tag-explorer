<script setup lang="ts">
import { IMG, VIDEO } from '.';

import { useRouter } from 'vue-router'
import { ResourceItem } from '../../api/resource';

interface Props {
  resource: ResourceItem;
}
const props = defineProps<Props>();

const router = useRouter();

const gotoHandle = (resource: ResourceItem) => {
  if (IMG.includes(resource.type)) {
    window.open('/resource/images/' + resource.id, '_blank');
  } else if (VIDEO.includes(resource.type)) {
    const routeUrl = router.resolve({
      path: "/video",
      query: { id: resource.id }
    });
    window.open(routeUrl.href, '_blank');
    return '/resource/videos/' + resource.id;
  } else {
    window.open('/resource/download/' + resource.id, '_blank');
  }
}

const mousemoveHandle = (e: MouseEvent) => {
  if (props.resource.coverCount == 0 && props.resource.cover) return;

  if (e.target == null) return;
  const item = (e.target as HTMLElement);
  const itemRect: DOMRect = item.getBoundingClientRect()
  // 鼠标在封面上从左到右的偏移量 [0-1]
  const offset: number = Math.abs(e.clientX - itemRect.left) / itemRect.width;

  item.setAttribute('src', `/cover/${props.resource.id}?index=${Math.floor(props.resource.coverCount * offset)}`);
}

const mouseleaveHandle = (e: MouseEvent) => {
  if (e.target == null) return;
  const item = (e.target as HTMLElement);

  if (props.resource.cover) {
    item.setAttribute('src', `/resource/cover/${props.resource.id}`);
  } else {
    item.setAttribute('src', `/cover/${props.resource.id}`);
  }
}
</script>

<template>
  <div class="resource-container" @dblclick="gotoHandle(resource)">
    <div class="cover-container">
      <img class="cover" :src="props.resource.cover ? `/resource/cover/${resource.id}` : `/cover/${resource.id}`"
        :alt="resource.name" loading="lazy"
        @mousemove="mousemoveHandle"  @mouseleave="mouseleaveHandle">
    </div>
    <div class="title">{{ resource.name }}</div>
  </div>
</template>

<style scoped>
.resource-container {
  display: flex;
  flex-direction: column;
  justify-content: center;

  cursor: pointer;
  overflow: hidden;
}

.resource-container:hover .title {
  color: #eb4a76;
}

.cover {
  width: 100%;
  border-radius: 8px;
}

.title {
  text-align: center;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;

  color: #403f3f;
}
</style>