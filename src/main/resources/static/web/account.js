const{createApp}= Vue
console.log("hola2")
const app=createApp({
    data(){
        return {
            id:'',
            params:'',
            account:'',
            accounts:[ ],
            transactions:[ ],
           
        }
    },
    created(){
        
        this.getData()

    }, 
    methods:{
        async getData(){
            try{
                axios.get('http://localhost:8080/api/accounts')
                .then(elemento => {
                    this.accounts=elemento.data
                    this.params=new URLSearchParams(location.search)
                    this.id= this.params.get("id")
                    this.account=this.accounts.find(account=>account.id.toString()===this.id)
                    this.transactions=this.account.transactions.sort((x,y)=>y.id-x.id)
                    console.log(this.transactions)
                    this.amountFormat();
                })
            }catch(err){
                console.log(err)
            }
        },
        amountFormat(){
            this.transactions.forEach(element => {
                element.amount = element.amount.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                element.data=element.data.toString().replace('T', ' ')
            })
            this.account.balance=this.account.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
        }
    }
})
app.mount('#app')