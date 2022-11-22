<template>
	<div class="term-box">
		<div :id="boxId"></div>
	</div>
</template>

<script lang="ts" setup>
import {nextTick, ref} from 'vue'
import { Terminal } from 'xterm'
import "xterm/css/xterm.css";

import { v4 as uuidv4 } from 'uuid';
const boxId = ref(uuidv4())

const initTerm = () => {
	let termContainer:any = document.getElementById(boxId.value)
	const term = new Terminal({
		cursorBlink: true,
		cols: 100,
		theme: {
			// foreground: "#7e9192", //字体
			// fontFamily: 'DejaVu',
			// lineHeight: 16
		}
	})
	term.open(termContainer)
	term.write('Hello from \x1B[1;3;31mxterm.js\x1B[0m $ ')

}
nextTick(()=>{
	initTerm();
})


</script>