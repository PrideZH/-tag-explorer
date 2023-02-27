<script setup lang="ts">
import axios from 'axios';
import { ref, watch, nextTick } from 'vue';

import resourceApi, { Resource } from '../../api/resource';
import tagApi from '../../api/tag';

import StringUtil from '../../utils/StringUtil';

interface Props {
  id?: string;
}
const props = defineProps<Props>();

const resource = ref<Resource>();

const emits = defineEmits([ 'delete', 'update', 'delCover', 'insertCover' ])

const tagInputRef = ref();
const isInputTag = ref<boolean>(false);

const fileRef = ref();

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
    const name: string = inputEl.value.trim();
    
    emits('update', props.id, inputEl.value);
    resource.value.name = name
    resourceApi.put(props.id, {
        name: name
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
    let tag: string = inputEl.value.trim()
    tag = tag.toLowerCase();

    if (resource.value) {
        for (let i = 0; i < resource.value.tags.length; i++) {
            if (tag == resource.value.tags[i].name) {
                return;
            }
        }
    }

    tagApi.add({
        resourceId: props.id,
        name: tag
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

const uploadHandle = (event: Event) => {
    if (event.target === null) return;
    const input: HTMLInputElement = <HTMLInputElement>event.target;
    if (input.files === null) return;
    insertCover(input.files[0])
}

const delCoverHandle = () => {
    if (confirm('Do you really want to delete the cover?')) {
        if (props.id == undefined || resource.value == undefined) {
            console.error('Resource is loss');
            return;
        }
        resource.value.cover = false;
        emits('delCover', props.id);
        resourceApi.delCover(props.id)
    }
}

function requestResource (id: string) {
    resourceApi.get(id).then(res => {
        resource.value = res.data;
    })
}

function insertCover (file: File) {
    if (props.id == null) return;
    const formdata = new FormData();
    formdata.append('file', file);
    formdata.append('id', props.id);
    axios.post('/resource/cover', formdata, {
        headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => {
        if (resource.value == undefined) return;
        resource.value.cover = true;
        emits('insertCover', props.id)
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
    <div>Cover</div>
    <div class="covers">
        <div class="item custom" @click="() => fileRef.click()">
            <div v-if="resource?.cover" style="height: 100%">
                <img class="cover" :src="`/resource/cover/${resource.id}`" :alt="resource?.name" loading="lazy">
                <button class="close" @click.stop.prevent="delCoverHandle">x</button>
            </div>
            <div class="insert-item" v-else style="height: 100%">
                <span class="text">自定义封面</span>
            </div>
        </div>
        <input type="file" ref="fileRef" accept="image/*" @change="uploadHandle" style="display: none;"/>
        <div class="item" v-for="index in resource?.coverCount">
            <img class="cover" :src="`/cover/${resource?.id}?index=${index}`" :alt="resource?.name" loading="lazy">
        </div>
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

.tag .close, .covers .close {
    width: 16px;
    margin-left: 4px;
    color: #646464;
    border: none;
    border-radius: 50%;
    background-color: transparent;
    cursor: pointer;
}

.covers .close {
    position: absolute;
    right: 2px;
    top: 2px;
}

.tag .close:hover, .covers .close:hover {
    color: #fff;
    background-color: #646464;
}

.covers {
    display: flex;
    align-items: center;
    width: 100%;
}

.covers .item {
    width: 141px;
    height: 80px;
}

.covers .item img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.covers .custom {
    position: relative;
    cursor: pointer;
}

.covers .custom .insert-item {
    display: flex;
    align-items: center;
    justify-content: center;
}

.covers .custom .insert-item .text {
    color: #888;
    font-size: 12px;
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