import { Component, OnInit, OnDestroy } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-carousel',
  standalone: false,
  templateUrl: './carousel.component.html',
  styleUrl: './carousel.component.scss',
  animations: [
    trigger('slide', [
      state('in', style({ transform: 'translateX(0)' })),
      state('out', style({ transform: 'translateX(-100%)' })),
      transition('in => out', animate('500ms ease-in-out')),
      transition('out => in', animate('500ms ease-in-out'))
    ])
  ]
})
export class CarouselComponent {

  selectedTab: number = 0;
  private shuffleSubscription: Subscription | null = null;

  // hardcoded for now
  artistes = [
    { name: 'DJ Echo', image: '/carousel/artiste1.jpg', bio: 'Ratatouille' },
    { name: 'Sophie Beats', image: '/carousel/artiste2.jpg', bio: 'Catty Henson' },
    { name: 'Lyrical Jay', image: '/carousel/artiste3.jpg', bio: 'Deervid Bowen' }
  ];

  partners = [
    { name: 'Butchers Brew', logo: '/carousel/partner1.jpg', description: 'Fresh, home-brewed concoctions' },
    { name: 'Silverball Bar', logo: 'carousel/partner2.avig', description: 'Best drinks in town' },
    { name: 'Hereafter Events', logo: '/carousel/partner3.jpg', description: 'Exclusive event partner' }
  ];

  currentIndex = 0;
  slideState = 'in';

  ngOnInit() {
    this.startCarousel();
  }

  startCarousel() {
    setInterval(() => {
      this.slideState = 'out';
      setTimeout(() => {
        this.currentIndex = (this.currentIndex + 1) % this.artistes.length;
        this.slideState = 'in';
      }, 500); // Match animation duration
    }, 5000); // Change every 5 seconds
  }

}
