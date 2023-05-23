const { createApp } = Vue
const app = createApp( {
    data(){
        return {
            clients:[ ],
            firstName:'',
            lastName:'',
            email:'',
            name:'',
            amount:'',
            payments:'',
            interest:''
        }
    },
    created(){
            this.getData()
    },
     methods: {
        // aca van las funciones
        getData(){
            try{
                axios.get('http://localhost:8080/rest/clients')
                .then(elemento => {
                   this.clients=elemento.data._embedded.clients
                   })
             }catch{
                console.log(err)
             }
        },
        addClient(){
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
        createLoan(){
            Swal.fire({
                title: 'Are you sure?',
                text: "Do you want to create a new loan?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, create it!'
              }).then((result) => {
                if (result.isConfirmed) {
                    const payments=this.payments.split(',').map(Number);
                    //this.interest=this.interest/100;
                    const loan={
                        name: this.name,
                        maxAmount:this.amount,
                        payments: payments,
                        interest: this.interest
                    }
                    axios.post('/api/loans/admin',loan)
                    .then(response =>{
                        Swal.fire({
                            title:'created!',
                            text:'Your loan has been created.',
                            icon:'success',
                            didOpen:()=>{
                                document.querySelector('.swal2-confirm').addEventListener('click', () =>{location.reload(true)})
                            }
                        }) 
                    }).catch(function (error) {
                       
                        if(error.response.status==400){
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: 'All fields must be completed',
                               
                              })
                        }
                        else if(error.response.status==500){
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: 'Verify that payments contain only numerical values',
                               
                              })
                        }
                        else{
                            Swal.fire({
                                icon: 'error',
                                title: 'Oops...',
                                text: error.response.data,
                               
                              })
                        }
                       
                    })
                }
              })
        },
        logOut(){
            Swal.fire({
                title: 'Are you sure?',
                text: "Do you want to log out?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes, log out!'
              }).then((result) => {
                if (result.isConfirmed) {
                    axios.post('/api/logout')
                    .then(response =>{
                        window.location.href='/web/index.html'
                    })
                    .cath(console.log("err"))
                }
              })
        },
    },
    computed : {
    }
})
app.mount('#app')