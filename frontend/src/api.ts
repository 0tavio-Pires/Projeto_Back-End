let csrf={header:'X-CSRF-TOKEN',token:''};
export class ApiError extends Error { constructor(message:string,public status:number){super(message);} }
export function setCsrf(token:string,header:string){csrf={token,header};}
export async function request<T>(path:string,body?:unknown,method=body===undefined?'GET':'POST'):Promise<T>{
  const response=await fetch(path,{method,credentials:'same-origin',headers:{...(body!==undefined?{'Content-Type':'application/json'}:{}),...(!['GET','HEAD'].includes(method)?{[csrf.header]:csrf.token}:{})},body:body===undefined?undefined:JSON.stringify(body)});
  if(!response.ok){const problem=await response.json().catch(()=>({}));throw new ApiError(problem.detail??(response.status===403?'Operação não permitida ou sessão expirada. Atualize a página.':`Falha na operação (${response.status}).`),response.status);}
  if(response.status===204)return undefined as T;
  return response.json() as Promise<T>;
}
export async function login(username:string,password:string){
  const token=await request<{header:string;token:string}>('/auth/csrf');setCsrf(token.token,token.header);
  const response=await fetch('/login',{method:'POST',credentials:'same-origin',headers:{'Content-Type':'application/x-www-form-urlencoded',[csrf.header]:csrf.token},body:new URLSearchParams({username,password})});
  if(!response.ok)throw new ApiError(response.status===401?'Usuário ou senha inválidos.':'Não foi possível entrar. Atualize a página e tente novamente.',response.status);
}
