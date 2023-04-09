const { createApp } = Vue
console.log("esta funcionando")
const app = createApp( {
    data(){
        return {
             accounts:[ ],
             id:'',
             params:'',
             client:'',
             clients:[ ],
             balances:[ ],
             numberAccounts:[ ],
             totalAccounts:0,
             percentages:[ ],
             infoAccounts:{}

        }
    },
    created(){
            this.getData()

    },
     methods: {
         async getData(){
                    try{
                        axios.get('http://localhost:8080/api/clients')
                        .then(elemento => {
                        console.log(elemento)
                        this.clients=elemento.data;
                        this.params=new URLSearchParams(location.search)
                        this.id= this.params.get("id");
                        console.log(this.id);
                        this.client=this.clients.find(client=>client.id.toString()===this.id)
                        console.log(this.client)
                        this.accounts=this.client.accounts
                         console.log(this.accounts);
                         this.balances=this.accounts.map(account=>account.balance)
                         this.numberAccounts=this.accounts.map(account=>account.number)
                         this.totalAccounts=this.balances.reduce((accumulator, currentValue)=>accumulator+=currentValue,0)
                         this.percentages=this.balances.map(balance=>(balance*100)/this.totalAccounts)
                         console.log(this.accounts.map(account=>account.creationDate.toString().replace('T', ' ')));
                         this.balanceFormat();
                           })
                     }catch{
                        console.log(err)
                     }
                },
                balanceFormat(){
                    this.accounts.forEach(element => {
                        element.balance = element.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                        element.creationDate=element.creationDate.toString().replace('T', ' ')
                        
                    })
                }    
        },

    computed : {


    }
})
app.mount('#app')
