import React from 'react';
import StarIcon from '@material-ui/icons/Star';
import StarHalfIcon from '@material-ui/icons/StarHalf';

interface Props {
    rating: number;
    totalStars?: number;
}

const Rating: React.FC<Props> = ({rating, totalStars = 5}) => {
    let fullStars: number;
    let hasHalfStar: boolean;

    if (rating === 5) {
        fullStars = 5;
        hasHalfStar = false;
    } else if (rating >= 4.5) {
        fullStars = 4;
        hasHalfStar = true;
    } else {
        fullStars = Math.floor(rating);
        hasHalfStar = rating % 1 !== 0;
    }

    const stars = [];
    for (let i = 0; i < fullStars; i++) {
        stars.push(<StarIcon key={`star-${i}`} className="star" style={{fill: "#fff400", fontSize: 30}}/>);
    }

    if (hasHalfStar) {
        stars.push(<StarHalfIcon key="half-star" className="star" style={{fill: "#fff400", fontSize: 30}}/>);
    }

    for (let i = fullStars + (hasHalfStar ? 1 : 0); i < totalStars; i++) {
        stars.push(<StarIcon key={`empty-star-${i}`} className="star" style={{fill: "#ffffff", fontSize: 30}}/>);
    }

    return (
        <div>
            {stars}
        </div>
    );
};

export default Rating;
