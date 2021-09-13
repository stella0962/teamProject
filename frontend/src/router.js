
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ClassManager from "./components/ClassManager"

import CourseManager from "./components/CourseManager"

import PaymentManager from "./components/PaymentManager"

import DeliveryManager from "./components/DeliveryManager"


import Mypage from "./components/Mypage"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/classes',
                name: 'ClassManager',
                component: ClassManager
            },

            {
                path: '/courses',
                name: 'CourseManager',
                component: CourseManager
            },

            {
                path: '/payments',
                name: 'PaymentManager',
                component: PaymentManager
            },

            {
                path: '/deliveries',
                name: 'DeliveryManager',
                component: DeliveryManager
            },


            {
                path: '/mypages',
                name: 'Mypage',
                component: Mypage
            },


    ]
})
