import { getAuth } from 'firebase/auth';


export const getIdToken = async() => {
    const auth = getAuth();
    return await auth.currentUser.getIdToken();
}
