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
                this.params=new URLSearchParams(location.search)
                this.id= this.params.get("id")
                axios.get('http://localhost:8080/api/accounts/'+ this.id)
                .then(elemento => {
                    console.log(elemento.data)
                    this.account=elemento.data
                    this.transactions=this.account.transactions.sort((x,y)=>y.id-x.id)
                    this.amountFormat();
                    this.account.balance=this.account.balance.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                    this.account.creationDate=this.account.creationDate.slice(0,10);
                })
            }catch(err){
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
        amountFormat(){
            this.transactions.forEach(element => {
                element.amount = element.amount.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
                element.data=element.data.toString().replace('T', ' ')
            })
           
        },
       
    }
})
app.mount('#app')