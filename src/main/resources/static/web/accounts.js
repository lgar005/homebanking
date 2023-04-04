const { createApp } = Vue
const app = createApp( {
    data(){
        return {
            accounts:[ ],
             firstName:'',
             lastName:'',
             email:'',
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
                        axios.get('http://localhost:8080/api/clients/1')
                        .then(elemento => {
                        this.accounts=elemento.data.accounts
                         this.firstName=elemento.data.firstName
                         this.lastName=elemento.data.lastName
                         this.email=elemento.data.email
                         this.balances=this.accounts.map(account=>account.balance)
                         this.numberAccounts=this.accounts.map(account=>account.number)
                         this.totalAccounts=this.balances.reduce((accumulator, currentValue)=>accumulator+=currentValue,0)
                         this.percentages=this.balances.map(balance=>(balance*100)/this.totalAccounts)
                           })
                     }catch{
                        console.log(err)
                     }
                }
        },

    computed : {


    }
})
app.mount('#app')
