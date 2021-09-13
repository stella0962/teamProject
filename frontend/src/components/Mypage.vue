<template>

<v-data-table
    :headers="headers"
    :items="mypage"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'Mypage',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "classId", value: "classId" },
            { text: "applyId", value: "applyId" },
            { text: "studentName", value: "studentName" },
            { text: "payStatus", value: "payStatus" },
            { text: "payMethod", value: "payMethod" },
            { text: "payAccount", value: "payAccount" },
            { text: "deliveryStatus", value: "deliveryStatus" },
            { text: "addr", value: "addr" },
            { text: "telephoneInfo", value: "telephoneInfo" },
            { text: "applyStaus", value: "applyStaus" },
        ],
        mypage : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/mypages')

      this.mypage = temp.data._embedded.mypages;

    },
    methods: {
    }
  }
</script>

