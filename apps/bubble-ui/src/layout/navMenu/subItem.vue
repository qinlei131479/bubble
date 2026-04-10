<template>
	<template v-for="val in chils">
		<el-sub-menu :index="val.path" :key="val.path" :class="{ 'is-active': isSubMenuActive(val) }" v-if="val.children && val.children.length > 0">
			<template #title>
				<SvgIcon :name="val.meta.icon" />
				<span>{{ other.setMenuI18n(val) }}</span>
			</template>
			<sub-item :chil="val.children" />
		</el-sub-menu>
		<template v-else>
			<el-menu-item :index="val.path" :key="val.path" :class="{ 'is-active': isItemActive(val.path) }">
				<template v-if="!val.meta.isLink || (val.meta.isLink && val.meta.isIframe)">
					<SvgIcon :name="val.meta.icon" />
					<span>{{ other.setMenuI18n(val) }}</span>
				</template>
				<template v-else>
					<a class="w100" @click.prevent="onALinkClick(val)">
						<SvgIcon :name="val.meta.icon" />
						{{ other.setMenuI18n(val) }}
					</a>
				</template>
			</el-menu-item>
		</template>
	</template>
</template>

<script setup lang="ts" name="navMenuSubItem">
import { computed } from 'vue';
import { RouteRecordRaw } from 'vue-router';
import other from '/@/utils/other';

// 定义父组件传过来的值
const props = defineProps({
	// 菜单列表
	chil: {
		type: Array<RouteRecordRaw>,
		default: () => [],
	},
});

const route = useRoute();

// 获取父级菜单数据
const chils = computed(() => {
	return <RouteItems>props.chil;
});

// 当前路由的实际路径（处理动态路由）
const activePath = computed(() => {
	const { path, meta } = route;
	const currentPath = meta?.isDynamic ? (meta.isDynamicPath as string) : path;
	const pathSplit = currentPath.split('/');
	// 隐藏的详情页面高亮到其父级菜单
	if (pathSplit.length >= 4 && meta?.isHide) return pathSplit.splice(0, 3).join('/');
	return currentPath;
});

// 判断叶子菜单项是否为当前活跃路由
const isItemActive = (itemPath: string): boolean => {
	return itemPath === activePath.value;
};

// 判断子菜单是否包含当前活跃路由（递归检查子项）
const isSubMenuActive = (item: RouteItem): boolean => {
	if (!item.children) return false;
	return item.children.some((child: RouteItem) => {
		if (child.path === activePath.value) return true;
		if (child.children) return isSubMenuActive(child);
		return false;
	});
};

// 打开外部链接
const onALinkClick = (val: RouteItem) => {
	other.handleOpenLink(val);
};
</script>
