echarts.min.js 最开始是用这个文件，smooth：true设置的line图好好的，后面来了需求需要用到radar图，
但是echarts.min.js这个js不支持，就下载了echarts.min2.js这个文件，但是这个在原line图上的smooth：true样
式会变形，所以，现在只能两个js共存


echarts.min.js：适用于line和柱状图
echarts.min2.js：使用radar雷达图，目前就1901用了这个js