<template>
	<el-tabs v-model="editableTabsValue" type="card" class="demo-tabs" editable @tab-remove="removeTab" @tab-add="addTab()">
		<el-tab-pane v-for="item in editableTabs" :key="item.name" :name="item.name" :lazy="true">
			<template #label>
		        <span>
		          <span>{{ item.title }}</span>
		        </span>
			</template>
			<component :is="item.com"></component>
		</el-tab-pane>
	</el-tabs>
</template>

<script lang="ts" setup>
import {defineAsyncComponent, ref, shallowRef} from 'vue'
let tabIndex = 1
const editableTabsValue = ref('SSH1')
const editableTabs = shallowRef([{
	title: `SSH1`,
	name: `SSH1`,
	com: defineAsyncComponent(() => import("./views/WebShell.vue"))
}])

const addTab = () => {
	const newTabName = `SSH${++tabIndex}`
	editableTabs.value.push({
		title: newTabName,
		name: newTabName,
		com: defineAsyncComponent(() => import("./views/WebShell.vue"))
	})
	editableTabsValue.value = newTabName
}
const removeTab = (targetName: string) => {
	const tabs = editableTabs.value
	let activeName = editableTabsValue.value
	if (activeName === targetName) {
		tabs.forEach((tab, index) => {
			if (tab.name === targetName) {
				const nextTab = tabs[index + 1] || tabs[index - 1]
				if (nextTab) {
					activeName = nextTab.name
				}
			}
		})
	}
	editableTabsValue.value = activeName
	editableTabs.value = tabs.filter((tab) => tab.name !== targetName)
}
</script>

<style lang="scss" scoped></style>
