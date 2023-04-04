const { createApp } = Vue
const app = createApp( {
    data(){
        return {
            clients:[ ],
            firstName:'',
            lastName:'',
            email:'',
        }
    },
    created(){
            this.getData()
    },
     methods: {
        // aca van las funciones
        async getData(){
            try{
                axios.get('http://localhost:8080/rest/clients')
                .then(elemento => {
                   this.clients=elemento.data._embedded.clients
                   })
             }catch{
                console.log(err)
             }
        },
        async addClient(){
             if(this.firstName.length>1 && this.lastName.length>1 && this.email.length>1 ){
                  this.postClient()
             }
        },
        postClient(){
          axios({
            method:'post',
            url:'http://localhost:8080/rest/clients',
            data:{
                firstName: this.firstName,
                lastName: this.lastName,
                email: this.email
            }
          });
        },
    },
    computed : {
    }
})
app.mount('#app')