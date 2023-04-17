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

        }
    },
    created(){
            this.getData()

    },
     methods: {
         async getData(){
                    try{
                        this.params=new URLSearchParams(location.search)
                        this.id= this.params.get("id");  
                        console.log(this.id) 
                        axios.get('http://localhost:8080/api/clients/'+ this.id)
                        .then(elemento => {    
                        console.log(elemento.data)                   
                        this.client=elemento.data 
                        this.cards=this.client.cards
                        console.log(this.client.cards)   
                        this.cardsDebit=this.client.cards.filter(card=> card.type=="DEBIT") 
                        console.log(this.cardsDebit)  
                        this.cardsCredit=this.client.cards.filter(card=> card.type=="CREDIT")  
                        console.log(this.cardsCredit) 
                        this.cardNumberMatrix();
                        console.log(this.cards)            
                        
                        })
                     }catch{
                        console.log(err)
                     }
                },
                cardNumberMatrix(){
                    this.cards.forEach(card => {
                        card.number=card.number.split('-');
                    });
                }
                 
        },


})
app.mount('#app')
