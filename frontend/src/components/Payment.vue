<template>

<v-card style="width:300px; margin-left:5%;" outlined>
    <template slot="progress">
      <v-progress-linear
        color="deep-purple"
        height="10"
        indeterminate
      ></v-progress-linear>
    </template>

    <v-img
      style="width:290px; height:150px; border-radius:10px; position:relative; margin-left:5px; top:5px;"
      src="https://cdn.vuetifyjs.com/images/cards/cooking.png"
    ></v-img>

    <v-card-title v-if="value._links">
        Payment # {{value._links.self.href.split("/")[value._links.self.href.split("/").length - 1]}}
    </v-card-title >
    <v-card-title v-else>
        Payment
    </v-card-title >

    <v-card-text style = "margin-left:-15px; margin-top:10px;">

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="ApplyId" v-model="value.applyId"/>
          </div>
          <div class="grey--text ml-4" v-else>
            ApplyId :  {{value.applyId }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="PayMethod" v-model="value.payMethod"/>
          </div>
          <div class="grey--text ml-4" v-else>
            PayMethod :  {{value.payMethod }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="PayAccount" v-model="value.payAccount"/>
          </div>
          <div class="grey--text ml-4" v-else>
            PayAccount :  {{value.payAccount }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="PayStaus" v-model="value.payStaus"/>
          </div>
          <div class="grey--text ml-4" v-else>
            PayStaus :  {{value.payStaus }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="Addr" v-model="value.addr"/>
          </div>
          <div class="grey--text ml-4" v-else>
            Addr :  {{value.addr }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="TelephoneInfo" v-model="value.telephoneInfo"/>
          </div>
          <div class="grey--text ml-4" v-else>
            TelephoneInfo :  {{value.telephoneInfo }}
          </div>

          <div class="grey--text ml-4" v-if="editMode" style = "margin-top:-20px;">
            <v-text-field label="StudentName" v-model="value.studentName"/>
          </div>
          <div class="grey--text ml-4" v-else>
            StudentName :  {{value.studentName }}
          </div>


    </v-card-text>

    <v-divider class="mx-4"></v-divider>

    <v-card-actions style = "position:absolute; right:0; bottom:0;">
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="edit"
        v-if="!editMode"
      >
        Edit
      </v-btn>
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="save"
        v-else
      >
        Save
      </v-btn>
      <v-btn
        color="deep-purple lighten-2"
        text
        @click="remove"
        v-if="!editMode"
      >
        Delete
      </v-btn>
    </v-card-actions>
  </v-card>


</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'Payment',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        date: new Date().toISOString().substr(0, 10),
    }),

    methods: {
      edit(){
        this.editMode = true;
      },
      async save(){
        try{
          var temp = null;

          if(this.isNew){
            temp = await axios.post(axios.fixUrl('/payments'), this.value)
          }else{
            temp = await axios.put(axios.fixUrl(this.value._links.self.href), this.value)
          }

          this.value = temp.data;

          this.editMode = false;
          this.$emit('input', this.value);

          if(this.isNew){
            this.$emit('add', this.value);
          }else{
            this.$emit('edit', this.value);
          }

        }catch(e){
          alert(e.message)
        }
      },
      async remove(){
        try{
          await axios.delete(axios.fixUrl(this.value._links.self.href))
          this.editMode = false;
          this.isDeleted = true;

          this.$emit('input', this.value);
          this.$emit('delete', this.value);

        }catch(e){
          alert(e.message)
        }
      },

    }
  }
</script>

