const { createApp } = Vue
console.log("esta funcionando")
const app = createApp( {
    data(){
        return {
             accounts:[ ],
             id:'',
             params:'',
             client:'',
             loading:true,
             cardsDebit:[ ],
             cardsCredit:[ ],
             cards:[ ],
             loading:true

        }
    },
    created(){
            this.getData()

    },
     methods: {
         async getData(){
                    try{
                        /*this.params=new URLSearchParams(location.search)
                        this.id= this.params.get("id");  
                        console.log(this.id) */
                        axios.get('/api/clients/current')
                        .then(elemento => {    
                        console.log(elemento.data)                   
                        this.client=elemento.data 
                        this.cards=this.client.cards
                        this.formatDate()
                        console.log(this.client.cards)   
                        this.cardsDebit=this.client.cards.filter(card=> card.type=="DEBIT") 
                        console.log(this.cardsDebit)  
                        this.cardsCredit=this.client.cards.filter(card=> card.type=="CREDIT")  
                        console.log(this.cardsCredit) 
                        this.cardNumberMatrix();
                        console.log(this.cards)  
                        this.loading=false;          
                        
                        })
                     }catch{
                        console.log(err)
                     }
                },
                logOut(){
                    /*axios.post('/api/logout')
                    .then(response =>{
                        window.location.href='/web/index.html'
                    })
                    .cath(console.log("err"))*/
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
                        //element.creationDate=element.creationDate.toString().replace('T', ' ')
                       
                        
                    })
                }
                 
        },


})
app.mount('#app')
