/* list op */

#ifndef LIST_H
#define LIST_H

#ifndef NULL
#define NULL ((void *)0)
#endif

struct list_head {
	struct list_head *next, *prev;
};

#define LIST_HEAD_INIT(name) { &(name), &(name) }

#define LIST_HEAD(name) \
	struct list_head name = LIST_HEAD_INIT(name)

static inline void INIT_LIST_HEAD(struct list_head *list)
{
	list->next = list;
	list->prev = list;
}

static inline void __list_add(struct list_head *pnew,
			      struct list_head *prev,
			      struct list_head *next)
{
	next->prev = pnew;
	pnew->next = next;
	pnew->prev = prev;
	prev->next = pnew;
}


/**
 * Insert a new entry after the specified head.
 * This is good for implementing stacks.
 */
static inline void list_add(struct list_head *pnew, struct list_head *head)
{
	__list_add(pnew, head, head->next);
}


/**
 * Insert a new entry before the specified head.
 * This is useful for implementing queues.
 */
static inline void list_add_tail(struct list_head *pnew, struct list_head *head)
{
	__list_add(pnew, head->prev, head);
}


/*
 * Delete a list entry by making the prev/next entries
 * point to each other.
 */
static inline void __list_del(struct list_head * prev, struct list_head * next)
{
	next->prev = prev;
	prev->next = next;
}

static inline void list_del(struct list_head *entry)
{
	__list_del(entry->prev, entry->next);
	entry->next = (struct list_head *)NULL;//LIST_POISON1;
	entry->prev = (struct list_head *)NULL;//LIST_POISON2;
}

/**
 * list_is_last - tests whether 
 */
static inline int list_is_last(const struct list_head *list,
				const struct list_head *head)
{
	return list->next == head;
}

/**
 * list_empty - tests whether a list is empty
 */
static inline int list_empty(const struct list_head *head)
{
	return head->next == head;
}


/**
 * container_of - cast a member of a structure out to the containing structure
 *
 */
#ifndef offsetof
#define offsetof(TYPE, MEMBER) ((intptr_t) &((TYPE *)0)->MEMBER)
#endif

#define container_of(ptr, type, member) ({			\
	const typeof( ((type *)0)->member ) *__mptr = (ptr);	\
	(type *)( (char *)__mptr - offsetof(type,member) );})

/**
 * list_entry - get the struct for this entry
 */
#define list_entry(ptr, type, member) \
	container_of(ptr, type, member)


/**
 * list_for_each	-	iterate over a list
 */
#define list_for_each(pos, head) \
	for (pos = (head)->next; prefetch(pos->next), pos != (head); pos = pos->next)

#define prefetch(x) (x)

/**
* list_for_each_entry	-	iterate over list of given type
*/
#define list_for_each_entry(pos, head, member)				\
	if(!list_empty(head))								\
		for (pos = list_entry((head)->next, typeof(*pos), member);	\
			 &pos->member != (head);	\
			 pos = list_entry(pos->member.next, typeof(*pos), member))



/**
 * double_list_for_each_entry	-	iterate over two lists of given type
 */
#define double_list_for_each_entry(posa, heada, posb, headb, member)				\
	if(!list_empty(heada) && !list_empty(headb))			\
		for (posa = list_entry((heada)->next, typeof(*posa), member), posb = list_entry((headb)->next, typeof(*posb), member);	\
			&posa->member != (heada) && &posb->member != (headb);	\
			posa = list_entry(posa->member.next, typeof(*posa), member), posb = list_entry(posb->member.next, typeof(*posb), member))



#endif /* LIST_H */

