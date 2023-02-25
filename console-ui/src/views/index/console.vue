<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';

import resourceApi, { Resource } from '../../api/resource';
import tagApi from '../../api/tag';

import StringUtil from '../../utils/StringUtil';

interface Props {
  id?: string;
}
const props = defineProps<Props>();

const resource = ref<Resource>();

const emits = defineEmits([ 'delete', 'update' ])

const tagInputRef = ref();
const isInputTag = ref<boolean>(false);

watch(() => props.id, (newId, _) => {
    if (newId == undefined) {
        resource.value == undefined;
        return;
    }
    requestResource(newId)
});

const changeNameHandle = (event: Event) => {
    if (props.id == undefined) {
        console.error('Resource is loss');
        return;
    }

    if (event.target == null || resource.value == undefined) return;
    const inputEl: HTMLInputElement = <HTMLInputElement> event.target;
    
    if (resource.value.name == inputEl.value) return;
    
    resourceApi.put(props.id, {
        name: inputEl.value
    }).then(res => {
        emits('update', inputEl.value);
    })
}

const deleteHandle = () => {
    if (confirm('Do you really want to delete the resource?')) {
        if (props.id == undefined) {
            console.error('Resource is loss');
            return;
        }
        emits('delete', props.id);
        resourceApi.delete(props.id);
    }
}

const addTagHandle = (event: Event) => {
    if (props.id == undefined) {
        console.error('Resource is loss');
        return;
    }

    if (event.target == null) return;
    const inputEl: HTMLInputElement = <HTMLInputElement> event.target;

    if (StringUtil.isBlank(inputEl.value)) {
        isInputTag.value = false;
        return;
    }

    tagApi.add({
        resourceId: props.id,
        name: inputEl.value
    }).then(res => {
        if (props.id) requestResource(props.id)
        inputEl.value = ''
        isInputTag.value = false;
    })
}

const delTagHandle = (tagId: string) => {
    if (props.id == undefined) {
        console.error('Resource is loss');
        return;
    }
    tagApi.delete(props.id, tagId).then(res => {
        if (props.id) requestResource(props.id)
    })
}

function requestResource (id: string) {
    resourceApi.get(id).then(res => {
        resource.value = res.data;
    })
}
</script>

<template>
  <div id="console-container" v-if="id != undefined">
    <div>Name</div>
    <input class="input" type="text" :value="resource?.name || '-'" @blur="changeNameHandle" />
    <div>Remark</div>
    <input class="input" type="text" placeholder="Input a remark" />
    <div>Tags</div>
    <div class="tags">
        <span class="tag" v-for="tag in resource?.tags" :key="tag.id">
            <span class="content">{{ tag.name }}</span>
            <button class="close" @click="delTagHandle(tag.id)">x</button>
        </span>
        <button class="add-tag" v-show="!isInputTag" @click="() => { isInputTag = true; nextTick(() => tagInputRef.focus()) }">+ New Tag</button>
        <input class="add-tag" ref="tagInputRef" v-show="isInputTag" type="text" @blur="addTagHandle">
    </div>
    <div>Info</div>
    <div class="info">
        <div class="item">
            <div class="key">Size</div>
            <div class="value"></div>
        </div>
        <div class="item">
            <div class="key">Content-Type</div>
            <div class="value">{{ resource?.type || '-' }}</div>
        </div>
        <div class="item">
            <div class="key">Last Modified</div>
            <div class="value"></div>
        </div>
    </div>
    <button class="botton">Export</button>
    <button class="botton">Download</button>
    <button class="botton delete-btn" @click="deleteHandle">Delete</button>
  </div>
</template>

<style scoped>
#console-container {
    padding: 32px;
}

.input {
    width: 100%;
    padding: 2px 4px;
    margin: 4px 0;
}

.tags .tag {
    display: inline-block;
    margin: 8px;
    padding: 2px 4px;
    border: none;
    border-radius: 4px;
    color: #646464;
    background-color: #f3f3f3;
    font-size: 0.8rem;
}

.tags .tag-active {
    background-color: #68c239;
}

.tags .add-tag {
    width: 80px;
    padding: 2px 4px;
    border: 1px solid #646464;
    border-radius: 4px;
    color: #646464;
    cursor: pointer;
}

.tag .close {
    width: 16px;
    margin-left: 4px;
    color: #646464;
    border: none;
    border-radius: 50%;
    background-color: transparent;
    cursor: pointer;
}

.tag .close:hover {
    color: #fff;
    background-color: #646464;
}

.info {
    font-size: 0.8rem;
}

.info .item {
    display: flex;
    justify-content: space-between;

    margin: 4px 0;
}

.botton {
    width: 100%;
    margin: 4px 0;
    background-color: transparent;
    border: 1px solid #dbdef0;
    border-radius: 6px;
    color: #0d0d0d;
    cursor: pointer;
    font-size: 16px;
    line-height: 1.5;
    padding: 4px;
    text-align: center;
    text-decoration: none;
}

.botton:hover {
    border: 1px solid #0d0d0d;
}

.delete-btn {
    border: 1px solid #f9a19a;
}

.delete-btn:hover {
    border: 1px solid #f44336;
    background-color: #fff8f7;
}
</style>