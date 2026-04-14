import {defineConfig} from 'vitepress'

export default defineConfig({
    title: 'Bubble',
    description: '智能体原生平台知识库',
    lang: 'zh-CN',
    lastUpdated: true,
    rewrites: {
        'wiki/index.md': 'index.md',
    },

    themeConfig: {
        logo: '/images/logo.svg',
        siteTitle: 'Bubble Docs',

        nav: [
            {text: '指南', link: '/wiki/guide/introduction'},
            {text: '架构', link: '/wiki/architecture/overview'},
            {text: '开发', link: '/wiki/development/backend'},
            {text: '运维', link: '/wiki/ops/docker'},
            {
                text: '版本',
                items: [
                    {text: '变更日志', link: '/wiki/changelog/index'},
                    {text: '参考手册', link: '/wiki/reference/env-variables'},
                    {text: 'OA 迁移计划', link: '/wiki/plans/oa-migration-plan'},
                ],
            },
        ],

        sidebar: {
            '/wiki/guide/': [
                {
                    text: '入门指南',
                    items: [
                        {text: '项目介绍', link: '/wiki/guide/introduction'},
                        {text: '快速开始', link: '/wiki/guide/quick-start'},
                        {text: '目录结构', link: '/wiki/guide/project-structure'},
                    ],
                },
            ],

            '/wiki/architecture/': [
                {
                    text: '架构设计',
                    items: [
                        {text: '架构总览', link: '/wiki/architecture/overview'},
                        {text: '端口与服务', link: '/wiki/architecture/ports'},
                        {text: '模块职责', link: '/wiki/architecture/modules'},
                        {text: '数据库设计', link: '/wiki/architecture/database'},
                    ],
                },
                {
                    text: '测试',
                    items: [
                        {text: '测试策略', link: '/wiki/architecture/strategy'},
                    ],
                },
            ],

            '/wiki/development/': [
                {
                    text: '开发指南',
                    items: [
                        {text: '后端开发', link: '/wiki/development/backend'},
                        {text: '前端开发', link: '/wiki/development/frontend'},
                        {text: '智能体开发', link: '/wiki/development/agentic'},
                    ],
                },
            ],

            '/wiki/ops/': [
                {
                    text: '运维部署',
                    items: [
                        {text: 'Docker 部署', link: '/wiki/ops/docker'},
                        {text: '脚本部署', link: '/wiki/ops/deploy-script'},
                        {text: '监控与告警', link: '/wiki/ops/monitoring'},
                    ],
                },
            ],

            '/wiki/changelog/': [
                {
                    text: '版本与迭代',
                    items: [
                        {text: '变更日志', link: '/wiki/changelog/index'},
                    ],
                },
            ],

            '/wiki/plans/': [
                {
                    text: 'OA 迁移计划',
                    items: [
                        {text: '迁移主计划', link: '/wiki/plans/oa-migration-plan'},
                        {text: '阶段 1: 用户与组织', link: '/wiki/plans/phase-01-user-org'},
                        {text: '阶段 2: 配置与系统', link: '/wiki/plans/phase-02-system-config'},
                        {text: '阶段 3: HR/人事', link: '/wiki/plans/phase-03-hr'},
                        {text: '阶段 4: OA 与工作流', link: '/wiki/plans/phase-04-oa-workflow'},
                        {text: '阶段 5: CRM 客户', link: '/wiki/plans/phase-05-crm'},
                        {text: '阶段 6: 财务', link: '/wiki/plans/phase-06-finance'},
                        {text: '阶段 7: 项目管理', link: '/wiki/plans/phase-07-project'},
                        {text: '阶段 8: 低代码', link: '/wiki/plans/phase-08-lowcode'},
                        {text: '阶段 9: 辅助功能', link: '/wiki/plans/phase-09-auxiliary'},
                        {text: '阶段 10: 开放 API', link: '/wiki/plans/phase-10-openapi'},
                    ],
                },
            ],

            '/wiki/reference/': [
                {
                    text: '参考手册',
                    items: [
                        {text: '环境变量', link: '/wiki/reference/env-variables'},
                        {text: '错误码', link: '/wiki/reference/error-codes'},
                        {text: 'API约定', link: '/wiki/reference/api-conventions'},
                    ],
                },
            ],
        },

        // socialLinks: [
        //   { icon: 'github', link: 'https://github.com/your-org/Bubble' },
        // ],
        // editLink: {
        //   pattern: 'https://github.com/your-org/Bubble/edit/JDK17_master/docs/:path',
        //   text: '在 GitHub 上编辑此页',
        // },
        // outline: false,
        outlineTitle: '大纲',

        footer: {
            message: 'Bubble - 智能体原生平台',
            copyright: 'Copyright © 2025-present BubbleCloud',
        },

        lastUpdated: {
            text: '最后更新时间',
            formatOptions: {
                dateStyle: 'long',
                timeStyle: 'medium'
            }
        },

        search: {
            provider: 'local'
        },

        docFooter: {
            prev: '上一页',
            next: '下一页'
        }
    },
})
