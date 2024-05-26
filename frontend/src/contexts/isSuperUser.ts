import {User} from '../model/shop/User.tsx';

export const isSuperUser = (user: User | null): boolean => {
    return user?.role === 'superuser';
};
