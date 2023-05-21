const { createApp } = Vue
console.log("esta funcionando")
const app = createApp( {
    data(){
        return {
             client:'',
             cards:[ ],
             loading:true,
             cardNumber: ''
        }
    },
    created(){
            this.getData()

    },
     methods: {
         async getData(){
                    try{
                        axios.get('/api/clients/current')
                        .then(elemento => {    
                        console.log(elemento.data)                   
                        this.client=elemento.data 
                        this.cards=this.client.cards.filter(card=>card.active)
                        console.log(this.cards)
                        this.loading=false;          
                        
                        })
                     }catch{
                        console.log(err)
                     }
                },
                deleteCard(){
                    Swal.fire({
                        title: 'Are you sure?',
                        text: "Do you want to delete a card?",
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        confirmButtonText: 'Yes, delete it!'
                      }).then((result) => {
                        if (result.isConfirmed) {
                            axios.patch('/api/clients/current/cards',`cardNumber=${this.cardNumber}`,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                            .then(response =>{
                                Swal.fire({
                                    title:'deleted!',
                                    text:'Your card has been deleted.',
                                    icon:'success',
                                    didOpen:()=>{
                                        document.querySelector('.swal2-confirm').addEventListener('click', () =>{window.location.href='/web/cards.html'})
                                    }
                                }) 
                            }).catch(function (error) {
                               
                                if(error.response.status==400){
                                    Swal.fire({
                                        icon: 'error',
                                        title: 'Oops...',
                                        text: 'All fields must be completed',
                                       
                                      })
                                }else{
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
                cardNumberMatrix(){
                    this.cards.forEach(card => {
                        card.number=card.number.split('-');
                    });
                },
                formatDate(){
                    this.cards.forEach(card => {
                        card.thruDate = card.thruDate.slice(0,10)
                    })
                }
                 
        },


})
app.mount('#app')
